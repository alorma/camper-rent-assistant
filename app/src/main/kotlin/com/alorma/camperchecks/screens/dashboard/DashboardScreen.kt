package com.alorma.camperchecks.screens.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alorma.camperchecks.ui.components.loading.FullscreenLoading
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar
import com.alorma.camperchecks.ui.responsive.ResponsiveCenteredContainer

@Composable
fun DashboardScreen(onSignOut: () -> Unit) {
  AppScaffold(
    topBar = {
      StyledTopAppBar(
        title = { Text(text = "Dashboard") },
        actions = {
          TextButton(onClick = onSignOut) {
            Text(text = "Sign out")
          }
        },
      )
    },
  ) { paddingValues ->
    ResponsiveCenteredContainer(
      modifier = Modifier.padding(paddingValues),
    ) {
      FullscreenLoading()
    }
  }
}
