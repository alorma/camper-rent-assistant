package com.alorma.camperchecks.screens.dashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar

@Composable
fun DashboardScreen() {
  AppScaffold(
    topBar = {
      StyledTopAppBar(
        title = { Text(text = "Dasboard") },
      )
    },
  ) {
    Text(text = "Dasboard")
  }
}