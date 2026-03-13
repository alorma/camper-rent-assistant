# Camper Rent Assistant — Backlog (Issues grouped by phases/features)

This app does NOT handle booking/renting. Renting happens in external providers (v1 default: Yescapa).
The app tracks: rentals metadata, checklists, vehicle condition (photos), renter info, contacts helpers, taxes, exports.

Suggested labels:
- type: epic, feature, story, task
- priority: P0, P1, P2
- area: foundations, auth, backend, vehicle, rentals, checklists, failures, contacts, taxes, export, settings

Milestones:
- v1 (MVP)
- v1.1 (quality)
- v1.5 (PDF)

---

## Phase 0 — Foundations (v1)

### Epic: Define v1 scope + Definition of Done
- Write acceptance criteria for v1 modules
- Define DoD checklist (offline-friendly, per-user data isolation, no crashes)

### Feature: Android app skeleton (Kotlin + Compose + Navigation)
- Base screens: Rentals, Taxes, Settings
- Theme + dark mode

### Task: Documentation baseline
- docs/vision.md
- docs/data-model.md
- docs/privacy.md (contacts + photos + data retention)

---

## Phase 1 — Firebase & Security (v1)

### Feature: Firebase Auth (Google Sign-In only)
Acceptance:
- Sign in/out
- Session persistence
- Block app data when signed out

### Feature: Firestore data layer (per-user scope)
- Use `/users/{uid}/...` structure
- Repositories for Vehicles, Rentals, Templates, etc.

### Task: Firestore + Storage security rules (user isolation)
Acceptance:
- Users can only read/write their own `/users/{uid}`

---

## Phase 2 — Vehicle (v1)

### Feature: Vehicle entity (v1 supports 1 vehicle; data model supports many)
Fields:
- name
- plate

### Story: First-run flow to create default vehicle
Acceptance:
- If none exists, force setup before creating rentals

### Story: Vehicle settings screen (edit name/plate)
Acceptance:
- Edits persist and reflect everywhere

---

## Phase 3 — Rentals tracking (v1)

### Feature: Rental entity (provider + referenceId)
NOT booking. Just tracking.
Fields:
- provider (v1 default: Yescapa)
- referenceId (required; unique per provider)
- vehicleId
- startAt, endAt
- renterName, renterPhone, renterNotes
- notes
Rules:
- prevent duplicate provider+referenceId

### Story: Rentals list (upcoming / past) + search by referenceId
Acceptance:
- Sorted lists
- Search works

### Story: Rental detail screen (hub)
Links to:
- Checklists
- Condition (failures/issues)
- Taxes
- Contacts actions

---

## Phase 4 — Checklists (v1)

### Feature: Checklist templates editor (5 phases)
Phases:
- Pre-work
- Renting day
- During renting
- End day
- After rent
Template operations:
- add/edit/delete/reorder

### Story: Generate per-rental checklist instances from templates
- On rental creation, copy templates -> rental tasks

### Story: Rental checklist UI
- Check/uncheck
- Add custom task ("Other task")
- Reorder

### Feature: Auto-cleanup of rental task instances (no history)
- User setting: retentionDays (default 60)
- After `endAt + retentionDays`, delete rental task instances
Notes:
- Keep rental core, taxes, renter info, and issues/damage

---

## Phase 5 — Failures / Condition with photos (v1)

### Feature: Vehicle known failures catalog (per vehicle) + photos
Fields:
- title, description, mustMention, photos[]

### Story: Rental condition screen — “mentioned known failures” per rental
- For each known failure, store per-rental mentioned flag (+ timestamp optional)

### Story: Rental issues/damage (per rental) + photos
- Add title/description/photos
- Kept long-term

---

## Phase 6 — Contacts helpers (v1)

### Feature: “Add renter to Contacts”
- Use Android insert-contact screen prefilled
- Suggested contact name: `Camper Rent - {provider} {referenceId} - {renterName}`
- Trigger: shown when rental is near start (configurable window)

### Feature: “Cleanup contact”
- Open contact/details to allow manual delete (reliable)

---

## Phase 7 — Taxes + Export (v1)

### Feature: Tax settings
- VAT/IVA % default 21
- Income-tax estimate % default 20

### Story: Rental income + declared flag
Per rental:
- incomeGross
- declared (yes/no)
- declaredAt optional
Computed:
- vatEstimate
- incomeTaxEstimate
- totalEstimate
- netEstimate

### Feature: Taxes summary screen + CSV export
- Filter by year
- Totals + per-rental rows
- Export CSV via share sheet

---

## Phase 8 — Settings & Quality (v1.1)

### Task: Settings screen polish
- retentionDays
- “near rental” window for contacts CTA (e.g. 2–4 days)
- tracker link (manual location check)

### Task: Accessibility pass
### Task: Localization scaffold (EN/ES)
### Task: Privacy policy improvements (Play Store ready)

---

## Phase 9 — v1.5 PDF (phased)

### Epic: Attach rental PDF to rental
- Upload PDF to storage, keep link on rental

### Feature: Extract fields from PDF + “confirm import”
Blocked until sample PDFs available.