// Mock firebase-admin and firebase-functions before importing the module under
// test, so that admin.initializeApp() and onDocumentCreated() don't throw
// during unit-test runs (no real Firebase project is available).
jest.mock('firebase-admin', () => ({
  initializeApp: jest.fn(),
  firestore: jest.fn(),
}));

jest.mock('firebase-functions/v2/firestore', () => ({
  onDocumentCreated: jest.fn((_path: string, handler: unknown) => handler),
}));

// Pull in admin solely for its Firestore type; the real module is mocked above.
import * as admin from 'firebase-admin';
import { copyTemplateChecklist } from './index';

// ---------------------------------------------------------------------------
// Minimal Firestore mock helpers
// ---------------------------------------------------------------------------

type PlainDoc = Record<string, unknown>;

interface MockBatch {
  set: jest.Mock;
  commit: jest.Mock;
  calls: Array<{ ref: { id: string }; data: PlainDoc }>;
}

interface MockDb {
  db: admin.firestore.Firestore;
  mocks: {
    usersCollection: { doc: jest.Mock };
    userDoc: { collection: jest.Mock };
    rentalsCollection: { doc: jest.Mock };
    rentalDoc: { collection: jest.Mock };
    templatesCollection: { get: jest.Mock };
  };
}

function makeMockBatch(): MockBatch {
  const calls: Array<{ ref: { id: string }; data: PlainDoc }> = [];
  return {
    calls,
    set: jest.fn((ref: { id: string }, data: PlainDoc) => {
      calls.push({ ref, data });
    }),
    commit: jest.fn().mockResolvedValue(undefined),
  };
}

function makeMockDb(templates: PlainDoc[], batch: MockBatch): MockDb {
  let docCounter = 0;

  const checklistCollection = {
    doc: jest.fn(() => ({ id: `checklist-doc-${++docCounter}` })),
  };

  const rentalDoc = {
    collection: jest.fn(() => checklistCollection),
  };

  const rentalsCollection = {
    doc: jest.fn(() => rentalDoc),
  };

  const templateDocs = templates.map((t) => ({ data: () => t }));
  const templatesCollection = {
    get: jest.fn().mockResolvedValue({
      empty: templates.length === 0,
      docs: templateDocs,
    }),
  };

  const userDoc = {
    collection: jest.fn((name: string) => {
      if (name === 'checklistTemplates') return templatesCollection;
      if (name === 'rentals') return rentalsCollection;
      throw new Error(`Unexpected collection: ${name}`);
    }),
  };

  const usersCollection = {
    doc: jest.fn(() => userDoc),
  };

  const db = {
    collection: jest.fn((name: string) => {
      if (name === 'users') return usersCollection;
      throw new Error(`Unexpected top-level collection: ${name}`);
    }),
    batch: jest.fn(() => batch),
  };

  return {
    db: db as unknown as admin.firestore.Firestore,
    mocks: { usersCollection, userDoc, rentalsCollection, rentalDoc, templatesCollection },
  };
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

const UID = 'user-123';
const RENTAL_ID = 'rental-456';

describe('copyTemplateChecklist', () => {
  it('returns null and does not write anything when there are no templates', async () => {
    const batch = makeMockBatch();
    const { db } = makeMockDb([], batch);

    const result = await copyTemplateChecklist(UID, RENTAL_ID, db);

    expect(result).toBeNull();
    expect(batch.set).not.toHaveBeenCalled();
    expect(batch.commit).not.toHaveBeenCalled();
  });

  it('copies all valid templates into the rental checklist with checked=false', async () => {
    const templates: PlainDoc[] = [
      { id: 'tpl-1', phase: 'Before', title: 'Check tyres' },
      { id: 'tpl-2', phase: 'After', title: 'Clean interior' },
    ];

    const batch = makeMockBatch();
    const { db } = makeMockDb(templates, batch);

    const result = await copyTemplateChecklist(UID, RENTAL_ID, db);

    expect(result).toBeNull();
    expect(batch.set).toHaveBeenCalledTimes(2);
    expect(batch.commit).toHaveBeenCalledTimes(1);

    const writtenData = batch.calls.map((c) => c.data);
    expect(writtenData).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          phase: 'Before',
          title: 'Check tyres',
          checked: false,
          templateId: 'tpl-1',
        }),
        expect.objectContaining({
          phase: 'After',
          title: 'Clean interior',
          checked: false,
          templateId: 'tpl-2',
        }),
      ]),
    );
  });

  it('assigns a unique id to every new checklist item', async () => {
    const templates: PlainDoc[] = [
      { id: 'tpl-1', phase: 'Before', title: 'Item A' },
      { id: 'tpl-2', phase: 'Before', title: 'Item B' },
    ];

    const batch = makeMockBatch();
    const { db } = makeMockDb(templates, batch);

    await copyTemplateChecklist(UID, RENTAL_ID, db);

    const ids = batch.calls.map((c) => c.data['id']);
    expect(ids[0]).not.toEqual(ids[1]);
    // id stored inside the document must match the DocumentReference id
    batch.calls.forEach((c) => {
      expect(c.data['id']).toEqual(c.ref.id);
    });
  });

  it('skips templates that are missing required fields', async () => {
    const templates: PlainDoc[] = [
      { id: 'tpl-1', phase: 'Before' },           // missing title
      { id: 'tpl-2', title: 'No phase' },          // missing phase
      { phase: 'Renting', title: 'No id' },        // missing id
      { id: 'tpl-4', phase: 'StartDay', title: 'Valid item' },
    ];

    const batch = makeMockBatch();
    const { db } = makeMockDb(templates, batch);

    await copyTemplateChecklist(UID, RENTAL_ID, db);

    expect(batch.calls).toHaveLength(1);
    const writtenData = batch.calls[0]!.data;
    expect(writtenData['title']).toBe('Valid item');
    expect(writtenData['checked']).toBe(false);
  });

  it('commits an empty batch when all templates have missing fields', async () => {
    const templates: PlainDoc[] = [
      { id: 'tpl-1', phase: 'Before' },   // missing title
      { phase: 'After', title: 'No id' }, // missing id
    ];

    const batch = makeMockBatch();
    const { db } = makeMockDb(templates, batch);

    const result = await copyTemplateChecklist(UID, RENTAL_ID, db);

    expect(result).toBeNull();
    expect(batch.set).not.toHaveBeenCalled();
    expect(batch.commit).toHaveBeenCalledTimes(1);
  });

  it('scopes the template read to the triggering user', async () => {
    const templates: PlainDoc[] = [
      { id: 'tpl-1', phase: 'Before', title: 'Item' },
    ];

    const batch = makeMockBatch();
    const { db, mocks } = makeMockDb(templates, batch);

    await copyTemplateChecklist(UID, RENTAL_ID, db);

    // Path: users/{uid}/checklistTemplates
    expect(mocks.usersCollection.doc).toHaveBeenCalledWith(UID);
    expect(mocks.userDoc.collection).toHaveBeenCalledWith('checklistTemplates');
  });

  it('writes items into the correct rental checklist subcollection', async () => {
    const templates: PlainDoc[] = [
      { id: 'tpl-1', phase: 'EndDay', title: 'Return keys' },
    ];

    const batch = makeMockBatch();
    const { db, mocks } = makeMockDb(templates, batch);

    await copyTemplateChecklist(UID, RENTAL_ID, db);

    // Path: users/{uid}/rentals/{rentalId}/checklist
    expect(mocks.rentalsCollection.doc).toHaveBeenCalledWith(RENTAL_ID);
    expect(mocks.rentalDoc.collection).toHaveBeenCalledWith('checklist');
  });
});
