package com.alorma.camperchecks.screens.dashboard

import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.auth.AuthUser
import com.alorma.camperchecks.auth.Session
import com.alorma.camperchecks.auth.SessionState
import com.alorma.camperchecks.ui.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
  private val session: Session,
) : BaseViewModel<DashboardNavigation, DashboardNavigationSideEffect, DashboardSideEffect>() {
  val uiState: StateFlow<DashboardUiState> =
    session.state
      .map { state ->
        when (state) {
          is SessionState.Authenticated -> DashboardUiState(user = state.user)
          else -> DashboardUiState(user = null)
        }
      }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState(user = null),
      )

  override fun navigate(navigation: DashboardNavigation) = Unit

  fun onSignOut() {
    session.signOut()
  }
}

data class DashboardUiState(
  val user: AuthUser?,
)

sealed interface DashboardNavigation

sealed interface DashboardNavigationSideEffect

sealed interface DashboardSideEffect
