package com.alorma.camperchecks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.alorma.camperchecks.auth.Session
import com.alorma.camperchecks.auth.SessionState
import com.alorma.camperchecks.screens.addrental.AddRentalRoute
import com.alorma.camperchecks.screens.addrental.AddRentalScreen
import com.alorma.camperchecks.screens.rentalslist.RentalsListRoute
import com.alorma.camperchecks.screens.rentalslist.RentalsListScreen
import com.alorma.camperchecks.screens.login.LoginRoute
import com.alorma.camperchecks.screens.login.LoginScreen
import com.alorma.camperchecks.screens.onboarding.OnboardingRoute
import com.alorma.camperchecks.screens.onboarding.OnboardingScreen
import com.alorma.camperchecks.ui.components.loading.FullscreenLoading
import com.alorma.camperchecks.vehicle.VehicleDataSource
import kotlinx.coroutines.flow.combine
import org.koin.compose.koinInject

@Composable
fun App() {
  val session: Session = koinInject()
  val vehicleDataSource: VehicleDataSource = koinInject()

  val startKey by combine(session.state, vehicleDataSource.getVehicle()) { sessionState, vehicle ->
    when (sessionState) {
      SessionState.Loading -> null
      SessionState.Unauthenticated -> LoginRoute
      is SessionState.Authenticated -> if (vehicle == null) OnboardingRoute else RentalsListRoute
    }
  }.collectAsStateWithLifecycle(initialValue = null)

  when (val key = startKey) {
    null -> FullscreenLoading()
    else -> AppNavGraph(startKey = key)
  }
}

@Composable
private fun AppNavGraph(startKey: NavKey) {
  val appBackStack =
    retain(startKey::class.java.name) {
      mutableStateListOf<NavKey>(startKey)
    }

  NavDisplay(
    modifier = Modifier.fillMaxSize(),
    backStack = appBackStack,
    onBack = {
      if (appBackStack.size > 1) appBackStack.removeLast()
    },
    entryDecorators =
      listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
      ),
    entryProvider =
      entryProvider {
        entry<LoginRoute> {
          LoginScreen(
            onNavigateToRentalsList = {
              appBackStack.clear()
              appBackStack.add(RentalsListRoute)
            },
          )
        }
        entry<OnboardingRoute> {
          OnboardingScreen(
            onNavigateToRentalsList = {
              appBackStack.clear()
              appBackStack.add(RentalsListRoute)
            },
          )
        }
        entry<RentalsListRoute> {
          RentalsListScreen(
            onAddRental = { appBackStack.add(AddRentalRoute) },
          )
        }
        entry<AddRentalRoute> {
          AddRentalScreen(
            onNavigateBack = { appBackStack.removeLast() },
          )
        }
      },
  )
}
