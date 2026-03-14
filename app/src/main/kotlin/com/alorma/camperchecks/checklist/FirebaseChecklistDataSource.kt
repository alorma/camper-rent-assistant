package com.alorma.camperchecks.checklist

import com.alorma.camperchecks.firestore.UserFirestoreProvider
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseChecklistDataSource(
  private val firestoreProvider: UserFirestoreProvider,
) : ChecklistDataSource {

  override fun getItemsByRental(rentalId: String): Flow<List<ChecklistItem>> =
    callbackFlow {
      val listener =
        firestoreProvider.collection("checklists")
          .whereEqualTo("rentalId", rentalId)
          .addSnapshotListener { snapshot, error ->
            if (error != null) {
              Timber.e(error, "Error listening to checklists for rental $rentalId")
              trySend(emptyList())
              return@addSnapshotListener
            }
            trySend(snapshot?.documents?.mapNotNull { it.toChecklistItem() } ?: emptyList())
          }
      awaitClose { listener.remove() }
    }

  override suspend fun addItem(
    rentalId: String,
    phase: ChecklistPhase,
    title: String,
  ) {
    val doc = firestoreProvider.collection("checklists").document()
    doc.set(
      mapOf(
        "id" to doc.id,
        "rentalId" to rentalId,
        "phase" to phase.name,
        "title" to title,
      ),
    ).await()
  }

  override suspend fun updateItem(
    itemId: String,
    title: String,
  ) {
    firestoreProvider.collection("checklists")
      .document(itemId)
      .update("title", title)
      .await()
  }

  override suspend fun deleteItem(itemId: String) {
    firestoreProvider.collection("checklists")
      .document(itemId)
      .delete()
      .await()
  }

  private fun DocumentSnapshot.toChecklistItem(): ChecklistItem? {
    val id = getString("id") ?: return null
    val rentalId = getString("rentalId") ?: return null
    val phaseStr = getString("phase") ?: return null
    val title = getString("title") ?: return null
    val phase = runCatching { ChecklistPhase.valueOf(phaseStr) }.getOrNull() ?: return null
    return ChecklistItem(
      id = id,
      rentalId = rentalId,
      phase = phase,
      title = title,
    )
  }
}
