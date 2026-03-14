package com.alorma.camperchecks.screens.rentaldetail

import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.rental.Rental
import com.alorma.camperchecks.rental.RentalDataSource
import com.alorma.camperchecks.ui.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RentalDetailViewModel(
  rentalId: String,
  rentalDataSource: RentalDataSource,
) : BaseViewModel<RentalDetailNavigation, RentalDetailNavigationSideEffect, RentalDetailSideEffect>() {
  val uiState: StateFlow<RentalDetailUiState> =
    rentalDataSource
      .getRentalById(rentalId)
      .map { result -> RentalDetailUiState(rental = result.getOrNull()) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RentalDetailUiState(),
      )

  override fun navigate(navigation: RentalDetailNavigation) {
    when (navigation) {
      RentalDetailNavigation.Checklists -> {}
      RentalDetailNavigation.Condition -> {}
      RentalDetailNavigation.Taxes -> {}
      RentalDetailNavigation.Contacts -> {}
    }
  }
}

data class RentalDetailUiState(
  val rental: Rental? = null,
)

sealed interface RentalDetailNavigation {
  data object Checklists : RentalDetailNavigation

  data object Condition : RentalDetailNavigation

  data object Taxes : RentalDetailNavigation

  data object Contacts : RentalDetailNavigation
}

sealed interface RentalDetailNavigationSideEffect

sealed interface RentalDetailSideEffect
