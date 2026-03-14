import * as admin from 'firebase-admin';
import { onDocumentCreated } from 'firebase-functions/v2/firestore';

admin.initializeApp();

/**
 * Copies all checklist templates for a user into the new rental's `checklist`
 * subcollection. Each copied item gets `checked: false` so it is independently
 * editable per rental without modifying the original template.
 *
 * Returns null without writing anything when no templates exist.
 */
export async function copyTemplateChecklist(
  uid: string,
  rentalId: string,
  db: admin.firestore.Firestore,
): Promise<null> {
  // Fetch all checklist templates for this user
  const templatesSnapshot = await db
    .collection('users')
    .doc(uid)
    .collection('checklistTemplates')
    .get();

  // If no templates exist, nothing to copy
  if (templatesSnapshot.empty) {
    return null;
  }

  // Copy each template item into the rental's checklist subcollection
  const batch = db.batch();
  const checklistRef = db
    .collection('users')
    .doc(uid)
    .collection('rentals')
    .doc(rentalId)
    .collection('checklist');

  for (const templateDoc of templatesSnapshot.docs) {
    const template = templateDoc.data();
    const phase: unknown = template['phase'];
    const title: unknown = template['title'];
    const templateId: unknown = template['id'];

    // Skip documents that are missing required fields
    if (typeof phase !== 'string' || typeof title !== 'string' || typeof templateId !== 'string') {
      continue;
    }

    const newItemRef = checklistRef.doc();
    batch.set(newItemRef, {
      id: newItemRef.id,
      phase,
      title,
      checked: false,
      templateId,
    });
  }

  await batch.commit();
  return null;
}

export const copyTemplateChecklistOnRentalCreated = onDocumentCreated(
  { document: 'users/{uid}/rentals/{rentalId}', region: 'europe-west1' },
  async (event) => {
    const { uid, rentalId } = event.params;
    return copyTemplateChecklist(uid, rentalId, admin.firestore());
  },
);
