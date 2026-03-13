package com.alorma.camperchecks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.alorma.camperchecks.screens.dashboard.DashboardRoute
import com.alorma.camperchecks.screens.dashboard.DashboardScreen

@Composable
fun App() {
  val appBackStack = retain {
    mutableStateListOf<NavKey>(DashboardRoute)
  }

  NavDisplay(
    modifier = Modifier.fillMaxSize(),
    backStack = appBackStack,
    onBack = {
      if (appBackStack.size > 1) appBackStack.removeLast()
    },
    entryDecorators = listOf(
      rememberSaveableStateHolderNavEntryDecorator(),
      rememberViewModelStoreNavEntryDecorator(),
    ),
    entryProvider = entryProvider {
      entry<DashboardRoute> {
        DashboardScreen()
      }
    },
  )
}