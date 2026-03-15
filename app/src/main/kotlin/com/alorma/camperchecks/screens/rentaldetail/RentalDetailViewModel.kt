package com.alorma.camperchecks.screens.rentaldetail

import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.rental.Rental
import com.alorma.camperchecks.rental.RentalDataSource
import com.alorma.camperchecks.ui.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

class RentalDetailViewModel(
  rentalId: String,
  rentalDataSource: RentalDataSource,
) : BaseViewModel<RentalDetailNavigation, RentalDetailNavigationSideEffect, RentalDetailSideEffect>() {
  val uiState: StateFlow<RentalDetailUiState> =
    rentalDataSource
      .getRentalById(rentalId)
      .map { result ->
        val rental = result.getOrNull()
        if (rental != null) RentalDetailUiState.Loaded(rental) else RentalDetailUiState.Empty
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RentalDetailUiState.Loading,
      )

  override fun navigate(navigation: RentalDetailNavigation) {
    when (navigation) {
      RentalDetailNavigation.Checklists ->
        emitNavigationSideEffect(RentalDetailNavigationSideEffect.NavigateToChecklists)
      RentalDetailNavigation.Condition -> {}
      RentalDetailNavigation.Taxes -> {}
      RentalDetailNavigation.Contacts -> {}
    }
  }
}

sealed class RentalDetailUiState {
  data object Loading : RentalDetailUiState()
  data object Empty : RentalDetailUiState()
  data class Loaded(val rental: Rental) : RentalDetailUiState()
}

sealed interface RentalDetailNavigation {
  data object Checklists : RentalDetailNavigation

  data object Condition : RentalDetailNavigation

  data object Taxes : RentalDetailNavigation

  data object Contacts : RentalDetailNavigation
}

sealed interface RentalDetailNavigationSideEffect {
  data object NavigateToChecklists : RentalDetailNavigationSideEffect
}

sealed interface RentalDetailSideEffect
