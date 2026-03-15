package com.alorma.camperchecks.checklist

data class RentalChecklistItem(
  val id: String,
  val phase: ChecklistPhase,
  val title: String,
  val checked: Boolean,
  val templateId: String?,
)
