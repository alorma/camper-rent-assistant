package com.alorma.camperchecks.checklist

import kotlinx.coroutines.flow.Flow

interface ChecklistDataSource {
  fun getItemsByRental(rentalId: String): Flow<List<ChecklistItem>>

  suspend fun addItem(
    rentalId: String,
    phase: ChecklistPhase,
    title: String,
  )

  suspend fun updateItem(
    itemId: String,
    title: String,
  )

  suspend fun deleteItem(itemId: String)
}
