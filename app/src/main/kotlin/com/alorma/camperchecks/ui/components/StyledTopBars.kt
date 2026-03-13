package com.alorma.camperchecks.ui.components.topbar

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import com.alorma.camperchecks.icons.AppIcons
import com.alorma.camperchecks.icons.filled.Back
import com.alorma.camperchecks.ui.theme.preview.PreviewTheme

@Composable
fun NavigationIcon() {
  val localBackPress = LocalOnBackPressedDispatcherOwner.current

  IconButton(
    onClick = {
      localBackPress?.onBackPressedDispatcher?.onBackPressed()
    },
  ) {
    Icon(
      imageVector = AppIcons.Filled.Back,
      contentDescription = null,
    )
  }
}

@Composable
fun StyledTopAppBar(
  title: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
  expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
  windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
  colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  contentPadding: PaddingValues = TopAppBarDefaults.ContentPadding,
) {
  TopAppBar(
    title = title,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    contentPadding = contentPadding,
  )
}

@PreviewLightDark
@Composable
private fun StyledTopBarPreview() {
  PreviewTheme {
    StyledTopAppBarPreviewContent()
  }
}

@Composable
fun StyledTopAppBarPreviewContent() {
  StyledTopAppBar(
    title = { Text(text = "Preview") },
  )
}
