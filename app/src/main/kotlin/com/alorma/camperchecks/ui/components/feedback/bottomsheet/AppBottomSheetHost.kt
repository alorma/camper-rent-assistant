package com.alorma.camperchecks.ui.components.feedback.bottomsheet

import androidx.compose.runtime.Composable

@Composable
internal fun AppBottomSheetHost(ijBottomSheetState: AppBottomSheetState) {
  if (ijBottomSheetState.showSheet) {
    ijBottomSheetState.sheetContent()
  }
}
