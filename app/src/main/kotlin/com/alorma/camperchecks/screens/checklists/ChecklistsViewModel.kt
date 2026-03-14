package com.alorma.camperchecks.screens.checklists

import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.checklist.ChecklistDataSource
import com.alorma.camperchecks.checklist.ChecklistItem
import com.alorma.camperchecks.checklist.ChecklistPhase
import com.alorma.camperchecks.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChecklistsViewModel(
  private val rentalId: String,
  private val checklistDataSource: ChecklistDataSource,
) : BaseViewModel<ChecklistsNavigation, ChecklistsNavigationSideEffect, ChecklistsSideEffect>() {
  private val selectedPhase = MutableStateFlow(ChecklistPhase.PRE_WORK)
  private val dialogState = MutableStateFlow<ChecklistDialogState>(ChecklistDialogState.Hidden)

  val uiState: StateFlow<ChecklistsUiState> =
    combine(
      checklistDataSource
        .getItemsByRental(rentalId)
        .map { items -> items.groupBy { it.phase } },
      selectedPhase,
      dialogState,
    ) { itemsByPhase, phase, dialog ->
      ChecklistsUiState(
        selectedPhase = phase,
        itemsByPhase = itemsByPhase,
        dialogState = dialog,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = ChecklistsUiState(),
    )

  override fun navigate(navigation: ChecklistsNavigation) {
    when (navigation) {
      ChecklistsNavigation.Back ->
        emitNavigationSideEffect(ChecklistsNavigationSideEffect.NavigateBack)
    }
  }

  fun onPhaseSelected(phase: ChecklistPhase) {
    selectedPhase.value = phase
  }

  fun onAddItemClick() {
    dialogState.value = ChecklistDialogState.Adding
  }

  fun onEditItemClick(item: ChecklistItem) {
    dialogState.value = ChecklistDialogState.Editing(item)
  }

  fun onDeleteItem(item: ChecklistItem) {
    viewModelScope.launch {
      checklistDataSource.deleteItem(item.id)
    }
  }

  fun onSaveItem(title: String) {
    val trimmedTitle = title.trim()
    if (trimmedTitle.isBlank()) return
    viewModelScope.launch {
      when (val state = dialogState.value) {
        ChecklistDialogState.Adding ->
          checklistDataSource.addItem(rentalId, selectedPhase.value, trimmedTitle)
        is ChecklistDialogState.Editing ->
          checklistDataSource.updateItem(state.item.id, trimmedTitle)
        ChecklistDialogState.Hidden -> Unit
      }
      dialogState.value = ChecklistDialogState.Hidden
    }
  }

  fun onDismissDialog() {
    dialogState.value = ChecklistDialogState.Hidden
  }
}

data class ChecklistsUiState(
  val selectedPhase: ChecklistPhase = ChecklistPhase.PRE_WORK,
  val itemsByPhase: Map<ChecklistPhase, List<ChecklistItem>> = emptyMap(),
  val dialogState: ChecklistDialogState = ChecklistDialogState.Hidden,
) {
  val currentPhaseItems: List<ChecklistItem>
    get() = itemsByPhase[selectedPhase] ?: emptyList()
}

sealed interface ChecklistDialogState {
  data object Hidden : ChecklistDialogState

  data object Adding : ChecklistDialogState

  data class Editing(
    val item: ChecklistItem,
  ) : ChecklistDialogState
}

sealed interface ChecklistsNavigation {
  data object Back : ChecklistsNavigation
}

sealed interface ChecklistsNavigationSideEffect {
  data object NavigateBack : ChecklistsNavigationSideEffect
}

sealed interface ChecklistsSideEffect
