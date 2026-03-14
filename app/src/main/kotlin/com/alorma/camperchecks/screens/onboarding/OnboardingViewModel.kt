package com.alorma.camperchecks.screens.onboarding

import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.ui.base.BaseViewModel
import com.alorma.camperchecks.vehicle.VehicleDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class OnboardingViewModel(
  private val vehicleDataSource: VehicleDataSource,
) : BaseViewModel<OnboardingNavigation, OnboardingNavigationSideEffect, OnboardingSideEffect>() {
  private val _uiState = MutableStateFlow(OnboardingUiState())
  val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

  override fun navigate(navigation: OnboardingNavigation) {
    when (navigation) {
      OnboardingNavigation.VehicleSaved ->
        emitNavigationSideEffect(OnboardingNavigationSideEffect.NavigateToRentalsList)
    }
  }

  fun onNameChange(value: String) {
    _uiState.update { it.copy(name = value) }
  }

  fun onPlateChange(value: String) {
    _uiState.update { it.copy(plate = value) }
  }

  fun onSave() {
    val state = _uiState.value
    if (!state.isValid || state.isSaving) return
    viewModelScope.launch {
      _uiState.update { it.copy(isSaving = true) }
      runCatching {
        vehicleDataSource.saveVehicle(
          name = state.name.trim(),
          plate = state.plate.trim(),
        )
      }.onSuccess {
        navigate(OnboardingNavigation.VehicleSaved)
      }.onFailure { error ->
        Timber.e(error, "Failed to save vehicle")
        _uiState.update { it.copy(isSaving = false, hasError = true) }
      }
    }
  }

  fun onErrorDismissed() {
    _uiState.update { it.copy(hasError = false) }
  }
}

data class OnboardingUiState(
  val name: String = "",
  val plate: String = "",
  val isSaving: Boolean = false,
  val hasError: Boolean = false,
) {
  val isValid: Boolean get() = name.isNotBlank() && plate.isNotBlank()
}

sealed interface OnboardingNavigation {
  data object VehicleSaved : OnboardingNavigation
}

sealed interface OnboardingNavigationSideEffect {
  data object NavigateToRentalsList : OnboardingNavigationSideEffect
}

sealed interface OnboardingSideEffect
