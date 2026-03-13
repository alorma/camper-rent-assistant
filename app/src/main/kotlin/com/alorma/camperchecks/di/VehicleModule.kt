package com.alorma.camperchecks.di

import com.alorma.camperchecks.vehicle.FirebaseVehicleDataSource
import com.alorma.camperchecks.vehicle.VehicleDataSource
import org.koin.dsl.module

val vehicleModule =
  module {
    single<VehicleDataSource> {
      FirebaseVehicleDataSource(
        firestoreProvider = get(),
        session = get(),
      )
    }
  }
