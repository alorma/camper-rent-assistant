package com.alorma.camperchecks.rental

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface RentalDataSource {
  fun getRentals(): Flow<List<Rental>>

  suspend fun saveRental(
    provider: RentalProvider,
    referenceId: String,
    vehicleId: String,
    startAt: LocalDateTime,
    endAt: LocalDateTime,
    renterName: String,
    renterPhone: String?,
    renterNotes: String?,
    notes: String?,
  )
}
