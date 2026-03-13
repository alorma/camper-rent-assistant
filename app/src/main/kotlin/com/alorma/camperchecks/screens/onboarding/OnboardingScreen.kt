package com.alorma.camperchecks.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingScreen(
  onNavigateToDashboard: () -> Unit,
  viewModel: OnboardingViewModel = koinViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.navigationSideEffects.collect { effect ->
      when (effect) {
        OnboardingNavigationSideEffect.NavigateToDashboard -> onNavigateToDashboard()
      }
    }
  }

  AppScaffold { paddingValues ->
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
        text = "Set up your vehicle",
        style = AppTheme.typography.headlineMedium,
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Tell us about your camper to get started",
        style = AppTheme.typography.bodyMedium,
        color = AppTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(modifier = Modifier.height(40.dp))

      OutlinedTextField(
        value = uiState.name,
        onValueChange = viewModel::onNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Vehicle name") },
        placeholder = { Text("e.g. My Camper") },
        singleLine = true,
        keyboardOptions =
          KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next,
          ),
        enabled = !uiState.isSaving,
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = uiState.plate,
        onValueChange = viewModel::onPlateChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("License plate") },
        placeholder = { Text("e.g. ABC-1234") },
        singleLine = true,
        keyboardOptions =
          KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            imeAction = ImeAction.Done,
          ),
        enabled = !uiState.isSaving,
      )

      if (uiState.hasError) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "Something went wrong. Please try again.",
          color = AppTheme.colorScheme.error,
          style = AppTheme.typography.bodySmall,
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      if (uiState.isSaving) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
      } else {
        Button(
          onClick = viewModel::onSave,
          modifier = Modifier.fillMaxWidth(),
          enabled = uiState.isValid,
        ) {
          Text("Let's go")
        }
      }
    }
  }
}
