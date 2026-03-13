package com.alorma.camperchecks.di

import com.alorma.camperchecks.clock.AppClock
import com.alorma.camperchecks.clock.KotlinAppClock
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val appModule =
  module {
    includes(themeModule)
    includes(authModule)
    includes(firestoreModule)
    includes(vehicleModule)
    includes(onboardingModule)
    includes(dashboardModule)
    includes(rentalModule)

    single { Settings() }
    single<AppClock> { KotlinAppClock() }
  }
