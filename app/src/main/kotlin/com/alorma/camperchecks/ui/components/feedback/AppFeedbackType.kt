package com.alorma.camperchecks.ui.components.feedback

import androidx.compose.runtime.Composable
import com.alorma.camperchecks.ui.theme.AppTheme

sealed class AppFeedbackType {
  data object Success : AppFeedbackType()

  data object Info : AppFeedbackType()

  data object Error : AppFeedbackType()
}

@Suppress("ContentEmission")
@Composable
fun AppFeedbackType.vibrantColors(): ContainerColors =
  when (this) {
    AppFeedbackType.Success ->
      ContainerColors(
        container = AppTheme.colorScheme.primary,
        onContainer = AppTheme.colorScheme.onPrimary,
      )

    AppFeedbackType.Info ->
      ContainerColors(
        container = AppTheme.colorScheme.inverseSurface,
        onContainer = AppTheme.colorScheme.inverseOnSurface,
      )

    AppFeedbackType.Error ->
      ContainerColors(
        container = AppTheme.colorScheme.error,
        onContainer = AppTheme.colorScheme.onError,
      )
  }

@Suppress("ContentEmission")
@Composable
fun AppFeedbackType.softColors(): ContainerColors =
  when (this) {
    AppFeedbackType.Success ->
      ContainerColors(
        container = AppTheme.colorScheme.primaryContainer,
        onContainer = AppTheme.colorScheme.onPrimaryContainer,
      )

    AppFeedbackType.Info ->
      ContainerColors(
        container = AppTheme.colorScheme.surface,
        onContainer = AppTheme.colorScheme.onSurface,
      )

    AppFeedbackType.Error ->
      ContainerColors(
        container = AppTheme.colorScheme.errorContainer,
        onContainer = AppTheme.colorScheme.onErrorContainer,
      )
  }
