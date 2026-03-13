package com.alorma.camperchecks.di

import com.alorma.camperchecks.screens.onboarding.OnboardingViewModel
import org.koin.dsl.module

val onboardingModule =
  module {
    factory { OnboardingViewModel(get()) }
  }
