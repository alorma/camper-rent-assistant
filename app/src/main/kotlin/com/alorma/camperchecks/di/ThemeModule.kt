package com.alorma.camperchecks.di

import com.alorma.camperchecks.ui.theme.ThemePreferences
import com.alorma.camperchecks.ui.theme.ThemePreferencesImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val themeModule =
  module {
    singleOf(::ThemePreferencesImpl) {
      bind<ThemePreferences>()
    }
  }
