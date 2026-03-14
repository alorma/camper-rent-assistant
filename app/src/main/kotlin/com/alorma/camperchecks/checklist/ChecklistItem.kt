package com.alorma.camperchecks.checklist

data class ChecklistItem(
  val id: String,
  val rentalId: String,
  val phase: ChecklistPhase,
  val title: String,
)
