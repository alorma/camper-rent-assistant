package com.alorma.camperchecks.vehicle

import kotlinx.coroutines.flow.Flow

interface VehicleDataSource {
  fun getVehicle(): Flow<Vehicle?>

  fun getVehicleState(): Flow<VehicleState>

  suspend fun saveVehicle(
    name: String,
    plate: String,
  )
}
