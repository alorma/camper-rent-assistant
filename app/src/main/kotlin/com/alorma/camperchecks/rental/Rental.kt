package com.alorma.camperchecks.rental

import kotlinx.datetime.LocalDateTime

data class Rental(
  val id: String,
  val provider: RentalProvider,
  val referenceId: String,
  val vehicleId: String,
  val startAt: LocalDateTime,
  val endAt: LocalDateTime,
  val renterName: String,
  val renterPhone: String?,
  val renterNotes: String?,
  val notes: String?,
  val finished: Boolean,
)
