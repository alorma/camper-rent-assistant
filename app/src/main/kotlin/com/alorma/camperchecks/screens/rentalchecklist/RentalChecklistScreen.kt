package com.alorma.camperchecks.screens.rentalchecklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.camperchecks.R
import com.alorma.camperchecks.checklist.ChecklistPhase
import com.alorma.camperchecks.checklist.RentalChecklistItem
import com.alorma.camperchecks.screens.checklisttemplates.label
import com.alorma.camperchecks.ui.components.feedback.AppFeedbackType
import com.alorma.camperchecks.ui.components.feedback.dialog.DialogResult
import com.alorma.camperchecks.ui.components.feedback.dialog.rememberAppDialogState
import com.alorma.camperchecks.ui.components.loading.FullscreenLoading
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.NavigationIcon
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar
import com.alorma.camperchecks.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RentalChecklistScreen(
  rentalId: String,
  onNavigateBack: () -> Unit,
  viewModel: RentalChecklistViewModel = koinViewModel { parametersOf(rentalId) },
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val dialogState = rememberAppDialogState()

  LaunchedEffect(viewModel) {
    viewModel.navigationSideEffects.collect { effect ->
      when (effect) {
        RentalChecklistNavigationSideEffect.NavigateBack -> onNavigateBack()
      }
    }
  }

  LaunchedEffect(viewModel) {
    viewModel.sideEffects.collect { effect ->
      when (effect) {
        RentalChecklistSideEffect.ShowAddItemDialog -> {
          val phases = ChecklistPhase.values()
          var selectedPhase by mutableStateOf<ChecklistPhase>(ChecklistPhase.Before)
          var title by mutableStateOf("")

          val result =
            dialogState.showAlertDialog(
              title = { Text(stringResource(R.string.rental_checklist_dialog_add_title)) },
              content = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                  PrimaryScrollableTabRow(
                    selectedTabIndex = phases.indexOf(selectedPhase),
                    edgePadding = 0.dp,
                    divider = {},
                  ) {
                    phases.forEach { phase ->
                      Tab(
                        selected = phase == selectedPhase,
                        onClick = { selectedPhase = phase },
                        text = { Text(phase.label()) },
                      )
                    }
                  }
                  OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.rental_checklist_dialog_item_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                  )
                }
              },
              positiveButton = { Text(stringResource(R.string.ok)) },
              negativeButton = { Text(stringResource(R.string.cancel)) },
              type = AppFeedbackType.Info,
            )

          if (result == DialogResult.Positive) {
            viewModel.addItem(selectedPhase, title)
          }
        }
      }
    }
  }

  AppScaffold(
    dialogState = dialogState,
    topBar = {
      StyledTopAppBar(
        title = { Text(text = stringResource(R.string.rental_checklist_title)) },
        navigationIcon = { NavigationIcon() },
      )
    },
    floatingActionButton = {
      if (uiState !is RentalChecklistUiState.Loading) {
        ExtendedFloatingActionButton(
          onClick = viewModel::onAddItemClick,
          text = { Text(stringResource(R.string.rental_checklist_add_item)) },
          icon = {},
        )
      }
    },
  ) { paddingValues ->
    when (val state = uiState) {
      RentalChecklistUiState.Loading -> FullscreenLoading()
      is RentalChecklistUiState.Empty -> EmptyChecklistContent(paddingValues)
      is RentalChecklistUiState.Loaded ->
        ChecklistContent(
          itemsByPhase = state.itemsByPhase,
          onToggleItem = viewModel::onToggleItem,
          paddingValues = paddingValues,
        )
    }
  }
}

@Composable
private fun EmptyChecklistContent(paddingValues: PaddingValues) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding =
      PaddingValues(
        top = paddingValues.calculateTopPadding() + 48.dp,
        bottom = paddingValues.calculateBottomPadding() + 16.dp,
        start = 16.dp,
        end = 16.dp,
      ),
  ) {
    item {
      Text(
        text = stringResource(R.string.rental_checklist_empty_title),
        style = AppTheme.typography.headlineSmall,
        color = AppTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = stringResource(R.string.rental_checklist_empty_subtitle),
        style = AppTheme.typography.bodyMedium,
        color = AppTheme.colorScheme.outline,
      )
    }
  }
}

@Composable
private fun ChecklistContent(
  itemsByPhase: List<Pair<ChecklistPhase, List<RentalChecklistItem>>>,
  onToggleItem: (RentalChecklistItem) -> Unit,
  paddingValues: PaddingValues,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding =
      PaddingValues(
        top = paddingValues.calculateTopPadding(),
        bottom = paddingValues.calculateBottomPadding() + 16.dp,
      ),
  ) {
    itemsByPhase.forEach { (phase, items) ->
      item(key = "header_${phase::class.simpleName}") {
        PhaseHeader(phase = phase)
      }
      items(items, key = { it.id }) { item ->
        ChecklistItemRow(item = item, onToggle = { onToggleItem(item) })
      }
    }
  }
}

@Composable
private fun PhaseHeader(phase: ChecklistPhase) {
  Text(
    text = phase.label(),
    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
    style = AppTheme.typography.titleSmall,
    color = AppTheme.colorScheme.primary,
  )
  HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun ChecklistItemRow(
  item: RentalChecklistItem,
  onToggle: () -> Unit,
) {
  ListItem(
    headlineContent = {
      Text(
        text = item.title,
        color = if (item.checked) AppTheme.colorScheme.outline else AppTheme.colorScheme.onSurface,
      )
    },
    trailingContent = {
      Checkbox(
        checked = item.checked,
        onCheckedChange = { onToggle() },
      )
    },
  )
}
