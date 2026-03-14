package com.alorma.camperchecks.screens.rentaldetail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RentalDetailRoute(
  val rentalId: String,
) : NavKey
