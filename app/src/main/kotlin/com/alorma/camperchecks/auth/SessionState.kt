package com.alorma.camperchecks.auth

data class AuthUser(
  val uid: String,
  val displayName: String?,
  val email: String?,
  val photoUrl: String?,
)

sealed interface SessionState {
  data class Authenticated(
    val user: AuthUser,
  ) : SessionState

  data object Unauthenticated : SessionState

  data object Loading : SessionState
}
