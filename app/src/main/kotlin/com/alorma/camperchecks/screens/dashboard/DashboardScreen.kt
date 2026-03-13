package com.alorma.camperchecks.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.camperchecks.auth.AuthUser
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar
import com.alorma.camperchecks.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
  viewModel: DashboardViewModel = koinViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  AppScaffold(
    topBar = {
      StyledTopAppBar(
        title = { Text(text = "Dashboard") },
        actions = {
          TextButton(onClick = viewModel::onSignOut) {
            Text(text = "Sign out")
          }
        },
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      uiState.user?.let { UserInfoSection(it) }
    }
  }
}

@Composable
private fun UserInfoSection(user: AuthUser) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
      text = "Signed in",
      style = AppTheme.typography.labelMedium,
      color = AppTheme.colorScheme.primary,
    )
    user.displayName?.let {
      Text(text = it, style = AppTheme.typography.headlineMedium)
    }
    user.email?.let {
      Text(
        text = it,
        style = AppTheme.typography.bodyMedium,
        color = AppTheme.colorScheme.onSurfaceVariant,
      )
    }
    Text(
      text = "UID: ${user.uid}",
      style = AppTheme.typography.bodySmall,
      color = AppTheme.colorScheme.outline,
    )
  }
}
