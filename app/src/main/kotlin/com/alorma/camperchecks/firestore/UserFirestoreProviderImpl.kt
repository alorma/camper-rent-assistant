package com.alorma.camperchecks.firestore

import com.alorma.camperchecks.auth.Session
import com.alorma.camperchecks.auth.SessionState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class UserFirestoreProviderImpl(
  private val firestore: FirebaseFirestore,
  private val session: Session,
) : UserFirestoreProvider {
  override fun collection(name: String): CollectionReference {
    val uid =
      (session.state.value as? SessionState.Authenticated)?.user?.uid
        ?: error("Cannot access Firestore collection '$name': user not authenticated")
    return firestore.collection("users").document(uid).collection(name)
  }
}
