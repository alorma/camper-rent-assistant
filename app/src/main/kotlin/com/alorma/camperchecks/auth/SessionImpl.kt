package com.alorma.camperchecks.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

class SessionImpl(
  private val firebaseAuth: FirebaseAuth,
  scope: CoroutineScope,
) : Session {
  override val state: StateFlow<SessionState> =
    callbackFlow {
      val listener =
        FirebaseAuth.AuthStateListener { auth ->
          trySend(auth.currentUser.toSessionState())
        }
      firebaseAuth.addAuthStateListener(listener)
      awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.stateIn(
      scope = scope,
      started = SharingStarted.Eagerly,
      initialValue = firebaseAuth.currentUser.toSessionState(),
    )

  override fun signOut() {
    firebaseAuth.signOut()
  }

  private fun FirebaseUser?.toSessionState(): SessionState =
    if (this == null) {
      SessionState.Unauthenticated
    } else {
      SessionState.Authenticated(
        AuthUser(
          uid = uid,
          displayName = displayName,
          email = email,
          photoUrl = photoUrl?.toString(),
        ),
      )
    }
}
