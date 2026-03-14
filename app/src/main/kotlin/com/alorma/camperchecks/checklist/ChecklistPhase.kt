package com.alorma.camperchecks.checklist

sealed class ChecklistPhase {
  data object Before : ChecklistPhase()
  data object StartDay : ChecklistPhase()
  data object Renting : ChecklistPhase()
  data object EndDay : ChecklistPhase()
  data object After : ChecklistPhase()
  companion object {
    fun values(): List<ChecklistPhase> {
      return listOf(Before, StartDay, Renting, EndDay, After)
    }
  }
}
