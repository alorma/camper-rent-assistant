package com.alorma.camperchecks

import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.alorma.camperchecks.auth.Session
import com.alorma.camperchecks.auth.SessionState
import com.alorma.camperchecks.screens.login.LoginRoute
import com.alorma.camperchecks.screens.onboarding.OnboardingRoute
import com.alorma.camperchecks.screens.rentalslist.RentalsListRoute
import com.alorma.camperchecks.ui.base.BaseViewModel
import com.alorma.camperchecks.vehicle.VehicleDataSource
import com.alorma.camperchecks.vehicle.VehicleState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AppViewModel(
  session: Session,
  vehicleDataSource: VehicleDataSource,
) : BaseViewModel<AppNavigation, AppNavigationSideEffect, AppSideEffect>() {
  val startKey: StateFlow<NavKey?> =
    combine(session.state, vehicleDataSource.getVehicleState()) { sessionState, vehicleState ->
      when (sessionState) {
        SessionState.Loading -> null
        SessionState.Unauthenticated -> LoginRoute
        is SessionState.Authenticated ->
          when (vehicleState) {
            VehicleState.Loading -> null
            VehicleState.NotFound -> OnboardingRoute
            is VehicleState.Found -> RentalsListRoute
          }
      }
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = null,
    )

  override fun navigate(navigation: AppNavigation) = Unit
}

sealed interface AppNavigation

sealed interface AppNavigationSideEffect

sealed interface AppSideEffect
