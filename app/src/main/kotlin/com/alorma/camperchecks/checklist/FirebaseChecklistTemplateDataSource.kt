package com.alorma.camperchecks.checklist

import com.alorma.camperchecks.firestore.UserFirestoreProvider
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseChecklistTemplateDataSource(
  private val firestoreProvider: UserFirestoreProvider,
) : ChecklistTemplateDataSource {
  override fun getTemplates(): Flow<List<ChecklistTemplate>> =
    callbackFlow {
      val listener =
        firestoreProvider
          .collection("checklistTemplates")
          .addSnapshotListener { snapshot, error ->
            if (error != null) {
              Timber.e(error, "Error listening to checklist templates")
              trySend(emptyList())
              return@addSnapshotListener
            }
            trySend(snapshot?.documents?.mapNotNull { it.toChecklistTemplate() } ?: emptyList())
          }
      awaitClose { listener.remove() }
    }

  override suspend fun addTemplate(
    phase: ChecklistPhase,
    title: String,
  ) {
    val doc = firestoreProvider.collection("checklistTemplates").document()
    doc
      .set(
        mapOf(
          "id" to doc.id,
          "phase" to phaseToId(phase),
          "title" to title,
        ),
      ).await()
  }

  override suspend fun updateTemplate(
    templateId: String,
    title: String,
  ) {
    firestoreProvider
      .collection("checklistTemplates")
      .document(templateId)
      .update("title", title)
      .await()
  }

  override suspend fun deleteTemplate(templateId: String) {
    firestoreProvider
      .collection("checklistTemplates")
      .document(templateId)
      .delete()
      .await()
  }

  private fun DocumentSnapshot.toChecklistTemplate(): ChecklistTemplate? {
    val id = getString("id") ?: return null
    val phaseStr = getString("phase") ?: return null
    val title = getString("title") ?: return null
    val phase = phaseIdToPhase(phaseStr) ?: return null
    return ChecklistTemplate(
      id = id,
      phase = phase,
      title = title,
    )
  }
}
