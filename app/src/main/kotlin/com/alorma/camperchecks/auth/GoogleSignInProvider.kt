package com.alorma.camperchecks.auth

interface GoogleSignInProvider {
  suspend fun signIn(): Result<AuthUser>
}
