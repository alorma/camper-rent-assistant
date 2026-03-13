package com.alorma.camperchecks.di

import com.alorma.camperchecks.screens.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingModule =
  module {
    viewModelOf(::OnboardingViewModel)
  }
