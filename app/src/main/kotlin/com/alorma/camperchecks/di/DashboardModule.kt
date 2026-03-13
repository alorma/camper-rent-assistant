package com.alorma.camperchecks.di

import com.alorma.camperchecks.screens.addrental.AddRentalViewModel
import com.alorma.camperchecks.screens.rentalslist.RentalsListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dashboardModule =
  module {
    viewModelOf(::RentalsListViewModel)
    viewModelOf(::AddRentalViewModel)
  }
