package com.alorma.camperchecks.di

import com.alorma.camperchecks.rental.FirebaseRentalDataSource
import com.alorma.camperchecks.rental.RentalDataSource
import org.koin.dsl.module

val rentalModule =
  module {
    single<RentalDataSource> {
      FirebaseRentalDataSource(
        firestoreProvider = get(),
      )
    }
  }
