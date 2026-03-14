package com.alorma.camperchecks.di

import com.alorma.camperchecks.checklist.ChecklistDataSource
import com.alorma.camperchecks.checklist.ChecklistTemplateDataSource
import com.alorma.camperchecks.checklist.FirebaseChecklistDataSource
import com.alorma.camperchecks.checklist.FirebaseChecklistTemplateDataSource
import com.alorma.camperchecks.screens.checklists.ChecklistsViewModel
import com.alorma.camperchecks.screens.checklisttemplates.ChecklistTemplatesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val checklistModule =
  module {
    single<ChecklistDataSource> {
      FirebaseChecklistDataSource(firestoreProvider = get())
    }
    single<ChecklistTemplateDataSource> {
      FirebaseChecklistTemplateDataSource(firestoreProvider = get())
    }
    viewModelOf(::ChecklistsViewModel)
    viewModelOf(::ChecklistTemplatesViewModel)
  }
