package com.alorma.camperchecks.vehicle

sealed interface VehicleState {
  data object Loading : VehicleState
  data object NotFound : VehicleState
  data class Found(val vehicle: Vehicle) : VehicleState
}
