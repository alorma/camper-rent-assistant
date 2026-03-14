package com.alorma.camperchecks.di

import com.alorma.camperchecks.checklist.ChecklistDataSource
import com.alorma.camperchecks.checklist.FirebaseChecklistDataSource
import com.alorma.camperchecks.screens.checklists.ChecklistsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val checklistModule =
  module {
    single<ChecklistDataSource> {
      FirebaseChecklistDataSource(firestoreProvider = get())
    }
    viewModelOf(::ChecklistsViewModel)
  }
