package com.alorma.camperchecks.screens.rentaldetail

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.camperchecks.R
import com.alorma.camperchecks.icons.AppIcons
import com.alorma.camperchecks.icons.filled.ChevronRight
import com.alorma.camperchecks.rental.Rental
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.NavigationIcon
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar
import com.alorma.camperchecks.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RentalDetailScreen(
  rentalId: String,
  viewModel: RentalDetailViewModel = koinViewModel(parameters = { parametersOf(rentalId) }),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.navigationSideEffects.collect { effect ->

    }
  }

  AppScaffold(
    topBar = {
      StyledTopAppBar(
        title = {
          Text(
            text = uiState.rental?.referenceId ?: stringResource(R.string.rental_detail_title_fallback),
          )
        },
        navigationIcon = { NavigationIcon() },
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
      uiState.rental?.let { rental ->
        item(key = "rental_info") {
          RentalInfoCard(rental = rental)
        }
      }

      item(key = "actions_header") {
        SectionHeader(title = stringResource(R.string.rental_detail_section_actions))
      }

      item(key = "checklists") {
        HubActionItem(
          label = stringResource(R.string.rental_detail_action_checklists),
          onClick = { viewModel.navigate(RentalDetailNavigation.Checklists) },
        )
      }

      item(key = "condition") {
        HubActionItem(
          label = stringResource(R.string.rental_detail_action_condition),
          onClick = { viewModel.navigate(RentalDetailNavigation.Condition) },
        )
      }

      item(key = "taxes") {
        HubActionItem(
          label = stringResource(R.string.rental_detail_action_taxes),
          onClick = { viewModel.navigate(RentalDetailNavigation.Taxes) },
        )
      }

      item(key = "contacts") {
        HubActionItem(
          label = stringResource(R.string.rental_detail_action_contacts),
          onClick = { viewModel.navigate(RentalDetailNavigation.Contacts) },
        )
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
private fun RentalInfoCard(rental: Rental) {
  Card(
    modifier = Modifier.fillMaxWidth(),
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
private fun HubActionItem(
  label: String,
  onClick: () -> Unit,
) {
  ListItem(
    modifier = Modifier.clickable(onClick = onClick),
    headlineContent = { Text(text = label) },
    trailingContent = {
      Icon(
        imageVector = AppIcons.Filled.ChevronRight,
        contentDescription = null,
      )
    },
  )
}
