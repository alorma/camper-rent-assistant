package com.alorma.camperchecks

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar

@Composable
fun App() {
  AppScaffold(
    topBar = {
      StyledTopAppBar(
        title = { Text(text = "Dashboard") },
      )
    },
  ) {
    Text(text = "Hello world")
  }
}