package com.alorma.camperchecks.ui.theme.preview

import androidx.compose.runtime.Composable
import com.alorma.camperchecks.ui.theme.AppThemeContent
import com.alorma.camperchecks.ui.theme.ThemePreferencesNoOp

@Suppress("ModifierRequired")
@Composable
fun PreviewTheme(block: @Composable () -> Unit) {
  AppThemeContent(themePreferences = ThemePreferencesNoOp) { block() }
}
