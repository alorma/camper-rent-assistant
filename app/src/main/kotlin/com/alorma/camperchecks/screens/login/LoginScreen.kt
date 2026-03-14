package com.alorma.camperchecks.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import com.joyner.googlesignincomposelibrary.models.types.Outlined
import com.joyner.googlesignincomposelibrary.ui.GoogleSignInButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.camperchecks.R
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar
import com.alorma.camperchecks.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
  onNavigateToRentalsList: () -> Unit,
  viewModel: LoginViewModel = koinViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.navigationSideEffects.collect { effect ->
      when (effect) {
        LoginNavigationSideEffect.NavigateToRentalsList -> onNavigateToRentalsList()
      }
    }
  }

  AppScaffold(
    topBar = {
      StyledTopAppBar(title = { Text(stringResource(R.string.app_name)) })
    },
  ) { paddingValues ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Text(
        text = stringResource(R.string.login_welcome),
        style = AppTheme.typography.displayMedium,
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = stringResource(R.string.login_subtitle),
        style = AppTheme.typography.bodyLarge,
        color = AppTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(modifier = Modifier.height(48.dp))

      when (uiState) {
        LoginUiState.Loading -> {
          CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }

        LoginUiState.Error -> {
          Text(
            text = stringResource(R.string.login_error),
            color = AppTheme.colorScheme.error,
            style = AppTheme.typography.bodyMedium,
          )
          Spacer(modifier = Modifier.height(16.dp))
          SignInWithGoogleButton(onClick = viewModel::onSignInClick)
        }

        LoginUiState.Idle -> {
          SignInWithGoogleButton(onClick = viewModel::onSignInClick)
        }
      }
    }
  }
}

@Composable
private fun SignInWithGoogleButton(onClick: () -> Unit) {
  GoogleSignInButton(
    onClick = onClick,
    buttonType = Outlined(),
  )
}
