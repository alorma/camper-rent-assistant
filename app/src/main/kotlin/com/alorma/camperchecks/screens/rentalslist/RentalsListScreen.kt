package com.alorma.camperchecks.screens.rentalslist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.camperchecks.R
import com.alorma.camperchecks.rental.Rental
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar
import com.alorma.camperchecks.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RentalsListScreen(
  onAddRental: () -> Unit,
  onOpenRentalDetail: (rentalId: String) -> Unit,
  viewModel: RentalsListViewModel = koinViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.navigationSideEffects.collect { effect ->
      when (effect) {
        is RentalsListNavigationSideEffect.NavigateToRentalDetail ->
          onOpenRentalDetail(effect.rentalId)
      }
    }
  }

  AppScaffold(
    topBar = {
      StyledTopAppBar(
        title = { Text(text = stringResource(R.string.rentals_list_title)) },
        actions = {
          TextButton(onClick = viewModel::onSignOut) {
            Text(text = stringResource(R.string.rentals_list_sign_out))
          }
        },
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = onAddRental,
        text = { Text(stringResource(R.string.rentals_list_add_rental)) },
        icon = {},
      )
    },
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding =
        PaddingValues(
          top = paddingValues.calculateTopPadding() + 16.dp,
          bottom = paddingValues.calculateBottomPadding() + 16.dp,
          start = 16.dp,
          end = 16.dp,
        ),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      uiState.currentRental?.let { rental ->
        item(key = "current_header") {
          SectionHeader(title = stringResource(R.string.rentals_list_section_current))
        }
        item(key = "current_${rental.id}") {
          CurrentRentalCard(
            rental = rental,
            onClick = { viewModel.navigate(RentalsListNavigation.OpenRentalDetail(rental.id)) },
          )
        }
      }

      if (uiState.upcomingRentals.isNotEmpty()) {
        item(key = "upcoming_header") {
          SectionHeader(title = stringResource(R.string.rentals_list_section_upcoming))
        }
        items(uiState.upcomingRentals, key = { it.id }) { rental ->
          RentalListItem(
            rental = rental,
            onClick = { viewModel.navigate(RentalsListNavigation.OpenRentalDetail(rental.id)) },
          )
        }
      }

      if (uiState.pastRentals.isNotEmpty()) {
        item(key = "past_header") {
          SectionHeader(title = stringResource(R.string.rentals_list_section_past))
        }
        items(uiState.pastRentals, key = { it.id }) { rental ->
          RentalListItem(
            rental = rental,
            onClick = { viewModel.navigate(RentalsListNavigation.OpenRentalDetail(rental.id)) },
          )
        }
      }

      if (uiState.currentRental == null &&
        uiState.upcomingRentals.isEmpty() &&
        uiState.pastRentals.isEmpty()
      ) {
        item(key = "empty") {
          EmptyRentals()
        }
      }
    }
  }
}

@Composable
private fun SectionHeader(title: String) {
  Column {
    Text(
      text = title,
      style = AppTheme.typography.titleSmall,
      color = AppTheme.colorScheme.primary,
    )
    Spacer(modifier = Modifier.height(4.dp))
    HorizontalDivider()
  }
}

@Composable
private fun CurrentRentalCard(
  rental: Rental,
  onClick: () -> Unit,
) {
  Card(
    modifier =
      Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick),
    colors =
      CardDefaults.cardColors(
        containerColor = AppTheme.colorScheme.primaryContainer,
      ),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(
        text = rental.provider.displayName,
        style = AppTheme.typography.labelMedium,
        color = AppTheme.colorScheme.onPrimaryContainer,
      )
      Text(
        text = rental.referenceId,
        style = AppTheme.typography.headlineSmall,
        color = AppTheme.colorScheme.onPrimaryContainer,
      )
      Text(
        text = rental.renterName,
        style = AppTheme.typography.bodyMedium,
        color = AppTheme.colorScheme.onPrimaryContainer,
      )
      Text(
        text = "${rental.startAt.date} → ${rental.endAt.date}",
        style = AppTheme.typography.bodySmall,
        color = AppTheme.colorScheme.onPrimaryContainer,
      )
    }
  }
}

@Composable
private fun RentalListItem(
  rental: Rental,
  onClick: () -> Unit,
) {
  ListItem(
    modifier = Modifier.clickable(onClick = onClick),
    headlineContent = { Text(text = rental.referenceId) },
    supportingContent = { Text(text = "${rental.startAt.date} → ${rental.endAt.date}") },
    overlineContent = { Text(text = rental.provider.displayName) },
    trailingContent = { Text(text = rental.renterName) },
  )
}

@Composable
private fun EmptyRentals() {
  Column(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(vertical = 48.dp),
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = stringResource(R.string.rentals_list_empty_title),
      style = AppTheme.typography.headlineSmall,
      color = AppTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = stringResource(R.string.rentals_list_empty_subtitle),
      style = AppTheme.typography.bodyMedium,
      color = AppTheme.colorScheme.outline,
    )
  }
}
