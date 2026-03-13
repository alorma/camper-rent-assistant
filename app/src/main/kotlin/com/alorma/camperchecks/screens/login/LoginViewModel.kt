package com.alorma.camperchecks.screens.login

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.auth.GoogleSignInProvider
import com.alorma.camperchecks.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
  private val googleSignInProvider: GoogleSignInProvider,
) : BaseViewModel<LoginNavigation, LoginNavigationSideEffect, LoginSideEffect>() {
  private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
  val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

  override fun navigate(navigation: LoginNavigation) {
    when (navigation) {
      LoginNavigation.SignedIn -> emitNavigationSideEffect(LoginNavigationSideEffect.NavigateToDashboard)
    }
  }

  fun onSignInClick() {
    if (_uiState.value is LoginUiState.Loading) return
    viewModelScope.launch {
      _uiState.value = LoginUiState.Loading
      googleSignInProvider
        .signIn()
        .onSuccess {
          _uiState.value = LoginUiState.Idle
          navigate(LoginNavigation.SignedIn)
        }.onFailure { error ->
          when (error) {
            is GetCredentialCancellationException -> {
              // User cancelled — silent, just reset
              _uiState.value = LoginUiState.Idle
            }
            else -> {
              Timber.e(error, "Google Sign-In failed")
              _uiState.value = LoginUiState.Error
            }
          }
        }
    }
  }

  fun onErrorDismissed() {
    _uiState.value = LoginUiState.Idle
  }
}

sealed interface LoginUiState {
  data object Idle : LoginUiState

  data object Loading : LoginUiState

  data object Error : LoginUiState
}

sealed interface LoginNavigation {
  data object SignedIn : LoginNavigation
}

sealed interface LoginNavigationSideEffect {
  data object NavigateToDashboard : LoginNavigationSideEffect
}

sealed interface LoginSideEffect
