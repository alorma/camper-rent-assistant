package com.alorma.camperchecks.di

import org.koin.dsl.module

val appModule =
  module {
    includes(themeModule)
    includes(authModule)
  }
