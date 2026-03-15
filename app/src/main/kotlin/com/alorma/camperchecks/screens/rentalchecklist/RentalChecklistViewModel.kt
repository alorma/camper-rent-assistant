package com.alorma.camperchecks.screens.rentalchecklist

import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.checklist.ChecklistPhase
import com.alorma.camperchecks.checklist.RentalChecklistDataSource
import com.alorma.camperchecks.checklist.RentalChecklistItem
import com.alorma.camperchecks.ui.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RentalChecklistViewModel(
  private val rentalId: String,
  private val rentalChecklistDataSource: RentalChecklistDataSource,
) : BaseViewModel<RentalChecklistNavigation, RentalChecklistNavigationSideEffect, RentalChecklistSideEffect>() {
  val uiState: StateFlow<RentalChecklistUiState> =
    rentalChecklistDataSource
      .getChecklist(rentalId)
      .map { items ->
        if (items.isEmpty()) {
          RentalChecklistUiState.Empty
        } else {
          val byPhase = ChecklistPhase.values()
            .mapNotNull { phase ->
              val phaseItems = items.filter { it.phase == phase }
              if (phaseItems.isEmpty()) null else phase to phaseItems
            }
          RentalChecklistUiState.Loaded(itemsByPhase = byPhase)
        }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RentalChecklistUiState.Loading,
      )

  override fun navigate(navigation: RentalChecklistNavigation) {
    when (navigation) {
      RentalChecklistNavigation.Back ->
        emitNavigationSideEffect(RentalChecklistNavigationSideEffect.NavigateBack)
    }
  }

  fun onToggleItem(item: RentalChecklistItem) {
    viewModelScope.launch {
      rentalChecklistDataSource.setChecked(rentalId, item.id, !item.checked)
    }
  }

  fun onAddItemClick() {
    emitSideEffect(RentalChecklistSideEffect.ShowAddItemDialog)
  }

  fun addItem(phase: ChecklistPhase, title: String) {
    val trimmed = title.trim()
    if (trimmed.isBlank()) return
    viewModelScope.launch {
      rentalChecklistDataSource.addItem(rentalId, phase, trimmed)
    }
  }
}

sealed class RentalChecklistUiState {
  data object Loading : RentalChecklistUiState()
  data object Empty : RentalChecklistUiState()
  data class Loaded(
    val itemsByPhase: List<Pair<ChecklistPhase, List<RentalChecklistItem>>>,
  ) : RentalChecklistUiState()
}

sealed interface RentalChecklistNavigation {
  data object Back : RentalChecklistNavigation
}

sealed interface RentalChecklistNavigationSideEffect {
  data object NavigateBack : RentalChecklistNavigationSideEffect
}

sealed interface RentalChecklistSideEffect {
  data object ShowAddItemDialog : RentalChecklistSideEffect
}
