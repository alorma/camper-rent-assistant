package com.alorma.camperchecks.screens.checklisttemplates

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.alorma.camperchecks.R
import com.alorma.camperchecks.checklist.ChecklistPhase

@Composable
fun ChecklistPhase.label(): String =
  stringResource(
    when (this) {
      ChecklistPhase.Before -> R.string.checklist_phase_pre_work
      ChecklistPhase.StartDay -> R.string.checklist_phase_renting_day
      ChecklistPhase.Renting -> R.string.checklist_phase_during_renting
      ChecklistPhase.EndDay -> R.string.checklist_phase_end_day
      ChecklistPhase.After -> R.string.checklist_phase_after_rent
    },
  )
