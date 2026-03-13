package com.alorma.camperchecks.screens.rentalslist

import androidx.lifecycle.viewModelScope
import com.alorma.camperchecks.auth.Session
import com.alorma.camperchecks.clock.AppClock
import com.alorma.camperchecks.rental.Rental
import com.alorma.camperchecks.rental.RentalDataSource
import com.alorma.camperchecks.ui.base.BaseViewModel
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class RentalsListViewModel(
  private val session: Session,
  private val rentalDataSource: RentalDataSource,
  private val clock: AppClock,
) : BaseViewModel<RentalsListNavigation, RentalsListNavigationSideEffect, RentalsListSideEffect>() {

  val uiState: StateFlow<RentalsListUiState> =
    rentalDataSource.getRentals()
      .map { rentals -> buildUiState(rentals) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RentalsListUiState(),
      )

  override fun navigate(navigation: RentalsListNavigation) = Unit

  fun onSignOut() {
    session.signOut()
  }

  @OptIn(ExperimentalTime::class)
  private fun buildUiState(rentals: List<Rental>): RentalsListUiState {
    val now: LocalDateTime = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val current = rentals.firstOrNull { rental ->
      !rental.finished && rental.startAt <= now && rental.endAt >= now
    }

    val upcoming = rentals
      .filter { rental ->
        !rental.finished && rental.endAt >= now && rental.id != current?.id
      }
      .sortedBy { it.startAt }

    val past = rentals
      .filter { rental ->
        rental.finished || rental.endAt < now
      }
      .sortedByDescending { it.endAt }

    return RentalsListUiState(
      currentRental = current,
      upcomingRentals = upcoming,
      pastRentals = past,
    )
  }
}

data class RentalsListUiState(
  val currentRental: Rental? = null,
  val upcomingRentals: List<Rental> = emptyList(),
  val pastRentals: List<Rental> = emptyList(),
)

sealed interface RentalsListNavigation

sealed interface RentalsListNavigationSideEffect

sealed interface RentalsListSideEffect
