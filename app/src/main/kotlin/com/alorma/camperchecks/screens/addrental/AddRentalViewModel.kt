package com.alorma.camperchecks.screens.addrental

import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.rental.RentalDataSource
import com.alorma.camperchecks.rental.RentalProvider
import com.alorma.camperchecks.ui.base.BaseViewModel
import com.alorma.camperchecks.vehicle.VehicleDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import timber.log.Timber

class AddRentalViewModel(
  private val rentalDataSource: RentalDataSource,
  private val vehicleDataSource: VehicleDataSource,
) : BaseViewModel<AddRentalNavigation, AddRentalNavigationSideEffect, AddRentalSideEffect>() {
  private val _uiState = MutableStateFlow(AddRentalUiState())
  val uiState: StateFlow<AddRentalUiState> = _uiState.asStateFlow()

  override fun navigate(navigation: AddRentalNavigation) {
    when (navigation) {
      AddRentalNavigation.RentalSaved ->
        emitNavigationSideEffect(AddRentalNavigationSideEffect.NavigateBack)
    }
  }

  fun onReferenceIdChange(value: String) {
    _uiState.update { it.copy(referenceId = value) }
  }

  fun onRenterNameChange(value: String) {
    _uiState.update { it.copy(renterName = value) }
  }

  fun onRenterPhoneChange(value: String) {
    _uiState.update { it.copy(renterPhone = value) }
  }

  fun onRenterNotesChange(value: String) {
    _uiState.update { it.copy(renterNotes = value) }
  }

  fun onNotesChange(value: String) {
    _uiState.update { it.copy(notes = value) }
  }

  fun onStartDateSelected(date: LocalDate) {
    _uiState.update { state ->
      val time = state.startAt?.time ?: LocalTime(0, 0)
      state.copy(startAt = LocalDateTime(date, time))
    }
  }

  fun onStartTimeSelected(time: LocalTime) {
    _uiState.update { state ->
      val date = state.startAt?.date ?: return@update state
      state.copy(startAt = LocalDateTime(date, time))
    }
  }

  fun onEndDateSelected(date: LocalDate) {
    _uiState.update { state ->
      val time = state.endAt?.time ?: LocalTime(23, 59)
      state.copy(endAt = LocalDateTime(date, time))
    }
  }

  fun onEndTimeSelected(time: LocalTime) {
    _uiState.update { state ->
      val date = state.endAt?.date ?: return@update state
      state.copy(endAt = LocalDateTime(date, time))
    }
  }

  fun onSave() {
    val state = _uiState.value
    if (!state.isValid || state.isSaving) return
    viewModelScope.launch {
      _uiState.update { it.copy(isSaving = true) }
      runCatching {
        val vehicle =
          vehicleDataSource.getVehicle().first()
            ?: error("No vehicle found")
        rentalDataSource.saveRental(
          provider = RentalProvider.Yescapa,
          referenceId = state.referenceId.trim(),
          vehicleId = vehicle.id,
          startAt = state.startAt!!,
          endAt = state.endAt!!,
          renterName = state.renterName.trim(),
          renterPhone = state.renterPhone.trimToNullable(),
          renterNotes = state.renterNotes.trimToNullable(),
          notes = state.notes.trimToNullable(),
        )
      }.onSuccess {
        navigate(AddRentalNavigation.RentalSaved)
      }.onFailure { error ->
        Timber.e(error, "Failed to save rental")
        _uiState.update { it.copy(isSaving = false, hasError = true) }
      }
    }
  }

  fun onErrorDismissed() {
    _uiState.update { it.copy(hasError = false) }
  }

  private fun String.trimToNullable(): String? = trim().ifBlank { null }
}

data class AddRentalUiState(
  val referenceId: String = "",
  val startAt: LocalDateTime? = null,
  val endAt: LocalDateTime? = null,
  val renterName: String = "",
  val renterPhone: String = "",
  val renterNotes: String = "",
  val notes: String = "",
  val isSaving: Boolean = false,
  val hasError: Boolean = false,
) {
  val isValid: Boolean
    get() =
      referenceId.isNotBlank() &&
        startAt != null &&
        endAt != null &&
        renterName.isNotBlank() &&
        endAt >= startAt
}

sealed interface AddRentalNavigation {
  data object RentalSaved : AddRentalNavigation
}

sealed interface AddRentalNavigationSideEffect {
  data object NavigateBack : AddRentalNavigationSideEffect
}

sealed interface AddRentalSideEffect
