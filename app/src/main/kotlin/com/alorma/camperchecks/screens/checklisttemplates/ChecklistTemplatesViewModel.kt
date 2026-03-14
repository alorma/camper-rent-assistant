package com.alorma.camperchecks.screens.checklisttemplates

import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.checklist.ChecklistPhase
import com.alorma.camperchecks.checklist.ChecklistTemplate
import com.alorma.camperchecks.checklist.ChecklistTemplateDataSource
import com.alorma.camperchecks.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChecklistTemplatesViewModel(
  private val checklistTemplateDataSource: ChecklistTemplateDataSource,
) : BaseViewModel<
    ChecklistTemplatesNavigation,
    ChecklistTemplatesNavigationSideEffect,
    ChecklistTemplatesSideEffect,
  >() {
  private val selectedPhase = MutableStateFlow(ChecklistPhase.PRE_WORK)
  private val dialogState = MutableStateFlow<ChecklistTemplateDialogState>(ChecklistTemplateDialogState.Hidden)

  val uiState: StateFlow<ChecklistTemplatesUiState> =
    combine(
      checklistTemplateDataSource
        .getTemplates()
        .map { templates -> templates.groupBy { it.phase } },
      selectedPhase,
      dialogState,
    ) { templatesByPhase, phase, dialog ->
      ChecklistTemplatesUiState(
        selectedPhase = phase,
        templatesByPhase = templatesByPhase,
        dialogState = dialog,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = ChecklistTemplatesUiState(),
    )

  override fun navigate(navigation: ChecklistTemplatesNavigation) {
    when (navigation) {
      ChecklistTemplatesNavigation.Back ->
        emitNavigationSideEffect(ChecklistTemplatesNavigationSideEffect.NavigateBack)
    }
  }

  fun onPhaseSelected(phase: ChecklistPhase) {
    selectedPhase.value = phase
  }

  fun onAddItemClick() {
    dialogState.value = ChecklistTemplateDialogState.Adding
  }

  fun onEditItemClick(template: ChecklistTemplate) {
    dialogState.value = ChecklistTemplateDialogState.Editing(template)
  }

  fun onDeleteItem(template: ChecklistTemplate) {
    viewModelScope.launch {
      checklistTemplateDataSource.deleteTemplate(template.id)
    }
  }

  fun onSaveItem(title: String) {
    val trimmedTitle = title.trim()
    if (trimmedTitle.isBlank()) return
    viewModelScope.launch {
      when (val state = dialogState.value) {
        ChecklistTemplateDialogState.Adding ->
          checklistTemplateDataSource.addTemplate(selectedPhase.value, trimmedTitle)
        is ChecklistTemplateDialogState.Editing ->
          checklistTemplateDataSource.updateTemplate(state.template.id, trimmedTitle)
        ChecklistTemplateDialogState.Hidden -> Unit
      }
      dialogState.value = ChecklistTemplateDialogState.Hidden
    }
  }

  fun onDismissDialog() {
    dialogState.value = ChecklistTemplateDialogState.Hidden
  }
}

data class ChecklistTemplatesUiState(
  val selectedPhase: ChecklistPhase = ChecklistPhase.PRE_WORK,
  val templatesByPhase: Map<ChecklistPhase, List<ChecklistTemplate>> = emptyMap(),
  val dialogState: ChecklistTemplateDialogState = ChecklistTemplateDialogState.Hidden,
) {
  val currentPhaseTemplates: List<ChecklistTemplate>
    get() = templatesByPhase[selectedPhase] ?: emptyList()
}

sealed interface ChecklistTemplateDialogState {
  data object Hidden : ChecklistTemplateDialogState

  data object Adding : ChecklistTemplateDialogState

  data class Editing(
    val template: ChecklistTemplate,
  ) : ChecklistTemplateDialogState
}

sealed interface ChecklistTemplatesNavigation {
  data object Back : ChecklistTemplatesNavigation
}

sealed interface ChecklistTemplatesNavigationSideEffect {
  data object NavigateBack : ChecklistTemplatesNavigationSideEffect
}

sealed interface ChecklistTemplatesSideEffect
