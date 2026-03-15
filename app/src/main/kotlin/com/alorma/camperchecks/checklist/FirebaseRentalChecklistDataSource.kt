package com.alorma.camperchecks.checklist

import com.alorma.camperchecks.firestore.UserFirestoreProvider
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseRentalChecklistDataSource(
  private val firestoreProvider: UserFirestoreProvider,
  private val templateDataSource: ChecklistTemplateDataSource,
) : RentalChecklistDataSource {
  override fun getChecklist(rentalId: String): Flow<List<RentalChecklistItem>> =
    callbackFlow {
      val listener =
        firestoreProvider
          .rentalCollection(rentalId, "checklist")
          .addSnapshotListener { snapshot, error ->
            if (error != null) {
              Timber.e(error, "Error listening to checklist for rental $rentalId")
              trySend(emptyList())
              return@addSnapshotListener
            }
            trySend(snapshot?.documents?.mapNotNull { it.toRentalChecklistItem() } ?: emptyList())
          }
      awaitClose { listener.remove() }
    }

  override suspend fun setChecked(
    rentalId: String,
    itemId: String,
    checked: Boolean,
  ) {
    firestoreProvider
      .rentalCollection(rentalId, "checklist")
      .document(itemId)
      .update("checked", checked)
      .await()
  }

  override suspend fun addItem(
    rentalId: String,
    phase: ChecklistPhase,
    title: String,
  ) {
    val col = firestoreProvider.rentalCollection(rentalId, "checklist")
    val doc = col.document()
    doc
      .set(
        mapOf(
          "id" to doc.id,
          "phase" to templateDataSource.phaseToId(phase),
          "title" to title,
          "checked" to false,
        ),
      ).await()
  }

  private fun DocumentSnapshot.toRentalChecklistItem(): RentalChecklistItem? {
    val id = getString("id") ?: return null
    val phaseStr = getString("phase") ?: return null
    val title = getString("title") ?: return null
    val checked = getBoolean("checked") ?: false
    val templateId = getString("templateId")
    val phase = templateDataSource.phaseIdToPhase(phaseStr) ?: return null
    return RentalChecklistItem(
      id = id,
      phase = phase,
      title = title,
      checked = checked,
      templateId = templateId,
    )
  }
}
