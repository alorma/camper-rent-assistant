package com.alorma.camperchecks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.alorma.camperchecks.ui.theme.AndroidSystemBarsAppearance
import com.alorma.camperchecks.ui.theme.LocalSystemBarsAppearance

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()

    setContent {
      CompositionLocalProvider(
        LocalSystemBarsAppearance provides AndroidSystemBarsAppearance(this),
      ) {
        App()
      }
    }
  }
}
