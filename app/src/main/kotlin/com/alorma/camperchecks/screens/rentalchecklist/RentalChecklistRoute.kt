package com.alorma.camperchecks.screens.rentalchecklist

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RentalChecklistRoute(
  val rentalId: String,
) : NavKey
