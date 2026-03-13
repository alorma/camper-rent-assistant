package com.alorma.camperchecks.di

import com.alorma.camperchecks.screens.rentalslist.RentalsListViewModel
import org.koin.dsl.module

val dashboardModule =
  module {
    factory { RentalsListViewModel(get(), get(), get()) }
  }
