package com.alorma.camperchecks.checklist

import kotlinx.coroutines.flow.Flow

interface ChecklistTemplateDataSource {
  fun getTemplates(): Flow<List<ChecklistTemplate>>

  suspend fun addTemplate(
    phase: ChecklistPhase,
    title: String,
  )

  suspend fun updateTemplate(
    templateId: String,
    title: String,
  )

  suspend fun deleteTemplate(templateId: String)

  fun phaseToId(phase: ChecklistPhase): String {
    return when(phase) {
      ChecklistPhase.After -> "After"
      ChecklistPhase.Before -> "Before"
      ChecklistPhase.EndDay -> "EndDay"
      ChecklistPhase.Renting -> "Renting"
      ChecklistPhase.StartDay -> "StartDay"
    }
  }

  fun phaseIdToPhase(phase: String): ChecklistPhase? {
    return when(phase) {
      "After" -> ChecklistPhase.After
      "Before" -> ChecklistPhase.Before
      "EndDay" -> ChecklistPhase.EndDay
      "Renting" -> ChecklistPhase.Renting
      "StartDay" -> ChecklistPhase.StartDay
      else -> null
    }
  }
}
