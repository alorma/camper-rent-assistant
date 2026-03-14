package com.alorma.camperchecks.checklist

enum class ChecklistPhase(
  val displayName: String,
) {
  PRE_WORK("Pre-work"),
  RENTING_DAY("Renting day"),
  DURING_RENTING("During renting"),
  END_DAY("End day"),
  AFTER_RENT("After rent"),
}
