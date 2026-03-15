package com.alorma.camperchecks.checklist

import kotlinx.coroutines.flow.Flow

interface RentalChecklistDataSource {
  fun getChecklist(rentalId: String): Flow<List<RentalChecklistItem>>

  suspend fun setChecked(
    rentalId: String,
    itemId: String,
    checked: Boolean,
  )

  suspend fun addItem(
    rentalId: String,
    phase: ChecklistPhase,
    title: String,
  )
}
