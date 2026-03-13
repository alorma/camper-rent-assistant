package com.alorma.camperchecks.di

import com.alorma.camperchecks.vehicle.FirebaseVehicleDataSource
import com.alorma.camperchecks.vehicle.VehicleDataSource
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.koin.dsl.module

val vehicleModule =
  module {
    single { Firebase.firestore }

    single<VehicleDataSource> {
      FirebaseVehicleDataSource(
        firestore = get(),
        session = get(),
      )
    }
  }
