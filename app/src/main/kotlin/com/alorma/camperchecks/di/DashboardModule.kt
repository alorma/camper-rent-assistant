package com.alorma.camperchecks.di

import com.alorma.camperchecks.screens.dashboard.DashboardViewModel
import org.koin.dsl.module

val dashboardModule =
  module {
    factory { DashboardViewModel(get()) }
  }
