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
}
