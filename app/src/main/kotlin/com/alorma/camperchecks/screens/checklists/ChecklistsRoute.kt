package com.alorma.camperchecks.screens.checklists

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class ChecklistsRoute(
  val rentalId: String,
) : NavKey
