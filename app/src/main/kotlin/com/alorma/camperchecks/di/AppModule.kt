package com.alorma.camperchecks.di

import com.russhwolf.settings.Settings
import org.koin.dsl.module

val appModule =
  module {
    includes(themeModule)
    includes(authModule)
    includes(vehicleModule)
    includes(onboardingModule)
    includes(dashboardModule)

    single { Settings() }
  }
