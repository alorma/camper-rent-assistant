package com.alorma.camperchecks.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.MessageDigest
import java.security.SecureRandom

class GoogleSignInProviderImpl(
  private val context: Context,
  private val firebaseAuth: FirebaseAuth,
  private val webClientId: String,
) : GoogleSignInProvider {
  private val credentialManager = CredentialManager.create(context)

  override suspend fun signIn(): Result<AuthUser> =
    runCatching {
      val nonce = generateNonce()
      val idToken = fetchGoogleIdToken(nonce)
      firebaseSignIn(idToken)
    }

  /**
   * First try the bottom-sheet flow (authorized accounts only, auto-select).
   * If no authorized accounts are found, fall back to the full "Sign in with Google" button flow.
   */
  private suspend fun fetchGoogleIdToken(nonce: String): String =
    try {
      fetchWithAuthorizedAccounts(nonce)
    } catch (e: NoCredentialException) {
      Timber.d("No authorized accounts, falling back to full sign-in flow")
      fetchWithSignInButton(nonce)
    } catch (e: GetCredentialCancellationException) {
      throw e
    }

  private suspend fun fetchWithAuthorizedAccounts(nonce: String): String {
    val option =
      GetGoogleIdOption
        .Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(webClientId)
        .setAutoSelectEnabled(true)
        .setNonce(nonce)
        .build()

    val request =
      GetCredentialRequest
        .Builder()
        .addCredentialOption(option)
        .build()

    val result = credentialManager.getCredential(context = context, request = request)
    return GoogleIdTokenCredential.createFrom(result.credential.data).idToken
  }

  private suspend fun fetchWithSignInButton(nonce: String): String {
    val option =
      GetSignInWithGoogleOption
        .Builder(webClientId)
        .setNonce(nonce)
        .build()

    val request =
      GetCredentialRequest
        .Builder()
        .addCredentialOption(option)
        .build()

    val result = credentialManager.getCredential(context = context, request = request)
    return GoogleIdTokenCredential.createFrom(result.credential.data).idToken
  }

  private suspend fun firebaseSignIn(idToken: String): AuthUser {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val authResult = firebaseAuth.signInWithCredential(credential).await()
    val user = checkNotNull(authResult.user) { "Firebase sign-in succeeded but user is null" }
    return AuthUser(
      uid = user.uid,
      displayName = user.displayName,
      email = user.email,
      photoUrl = user.photoUrl?.toString(),
    )
  }

  /**
   * Generates a SHA-256-hashed nonce for replay-attack prevention.
   */
  private fun generateNonce(): String {
    val rawNonce = ByteArray(32).also { SecureRandom().nextBytes(it) }
    return MessageDigest
      .getInstance("SHA-256")
      .digest(rawNonce)
      .joinToString("") { "%02x".format(it) }
  }
}
