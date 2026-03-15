package com.alorma.camperchecks.di

import com.alorma.camperchecks.checklist.ChecklistTemplateDataSource
import com.alorma.camperchecks.checklist.FirebaseChecklistTemplateDataSource
import com.alorma.camperchecks.checklist.FirebaseRentalChecklistDataSource
import com.alorma.camperchecks.checklist.RentalChecklistDataSource
import com.alorma.camperchecks.screens.checklisttemplates.ChecklistTemplatesViewModel
import com.alorma.camperchecks.screens.rentalchecklist.RentalChecklistViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val checklistModule =
  module {
    single<ChecklistTemplateDataSource> {
      FirebaseChecklistTemplateDataSource(firestoreProvider = get())
    }
    single<RentalChecklistDataSource> {
      FirebaseRentalChecklistDataSource(firestoreProvider = get(), templateDataSource = get())
    }
    viewModelOf(::ChecklistTemplatesViewModel)
    viewModel { params ->
      RentalChecklistViewModel(
        rentalId = params.get(),
        rentalChecklistDataSource = get(),
      )
    }
  }
