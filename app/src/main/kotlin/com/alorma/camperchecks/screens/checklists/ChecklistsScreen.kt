package com.alorma.camperchecks.screens.checklists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.camperchecks.checklist.ChecklistItem
import com.alorma.camperchecks.checklist.ChecklistPhase
import com.alorma.camperchecks.icons.AppIcons
import com.alorma.camperchecks.icons.filled.Delete
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.NavigationIcon
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar
import com.alorma.camperchecks.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChecklistsScreen(
  rentalId: String,
  onNavigateBack: () -> Unit,
  viewModel: ChecklistsViewModel = koinViewModel(parameters = { parametersOf(rentalId) }),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.navigationSideEffects.collect { effect ->
      when (effect) {
        ChecklistsNavigationSideEffect.NavigateBack -> onNavigateBack()
      }
    }
  }

  AppScaffold(
    topBar = {
      StyledTopAppBar(
        title = { Text(text = "Checklists") },
        navigationIcon = { NavigationIcon() },
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = viewModel::onAddItemClick,
        text = { Text("Add item") },
        icon = {},
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = paddingValues.calculateTopPadding()),
    ) {
      PhaseTabRow(
        selectedPhase = uiState.selectedPhase,
        onPhaseSelected = viewModel::onPhaseSelected,
      )

      HorizontalDivider()

      ChecklistItemsList(
        items = uiState.currentPhaseItems,
        onEditItem = viewModel::onEditItemClick,
        onDeleteItem = viewModel::onDeleteItem,
        contentPadding = PaddingValues(
          bottom = paddingValues.calculateBottomPadding() + 88.dp,
          start = 16.dp,
          end = 16.dp,
        ),
      )
    }
  }

  when (val dialog = uiState.dialogState) {
    ChecklistDialogState.Hidden -> Unit
    ChecklistDialogState.Adding -> {
      AddEditItemDialog(
        title = "Add item",
        initialValue = "",
        onDismiss = viewModel::onDismissDialog,
        onConfirm = viewModel::onSaveItem,
      )
    }
    is ChecklistDialogState.Editing -> {
      AddEditItemDialog(
        title = "Edit item",
        initialValue = dialog.item.title,
        onDismiss = viewModel::onDismissDialog,
        onConfirm = viewModel::onSaveItem,
      )
    }
  }
}

@Composable
private fun PhaseTabRow(
  selectedPhase: ChecklistPhase,
  onPhaseSelected: (ChecklistPhase) -> Unit,
) {
  val phases = ChecklistPhase.entries
  val selectedIndex = phases.indexOf(selectedPhase)

  ScrollableTabRow(
    selectedTabIndex = selectedIndex,
    edgePadding = 0.dp,
  ) {
    phases.forEach { phase ->
      Tab(
        selected = phase == selectedPhase,
        onClick = { onPhaseSelected(phase) },
        text = { Text(text = phase.displayName) },
      )
    }
  }
}

@Composable
private fun ChecklistItemsList(
  items: List<ChecklistItem>,
  onEditItem: (ChecklistItem) -> Unit,
  onDeleteItem: (ChecklistItem) -> Unit,
  contentPadding: PaddingValues,
) {
  if (items.isEmpty()) {
    EmptyPhaseContent(contentPadding = contentPadding)
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = contentPadding,
    ) {
      items(items, key = { it.id }) { item ->
        ChecklistItemRow(
          item = item,
          onEdit = { onEditItem(item) },
          onDelete = { onDeleteItem(item) },
        )
      }
    }
  }
}

@Composable
private fun EmptyPhaseContent(contentPadding: PaddingValues) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(contentPadding)
      .padding(vertical = 48.dp),
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = "No items yet",
      style = AppTheme.typography.headlineSmall,
      color = AppTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = "Tap \"Add item\" to create a checklist item",
      style = AppTheme.typography.bodyMedium,
      color = AppTheme.colorScheme.outline,
    )
  }
}

@Composable
private fun ChecklistItemRow(
  item: ChecklistItem,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
) {
  ListItem(
    modifier = Modifier.clickable(onClick = onEdit),
    headlineContent = { Text(text = item.title) },
    trailingContent = {
      IconButton(onClick = onDelete) {
        Icon(
          imageVector = AppIcons.Filled.Delete,
          contentDescription = "Delete item",
        )
      }
    },
  )
}

@Composable
private fun AddEditItemDialog(
  title: String,
  initialValue: String,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit,
) {
  var text by rememberSaveable { mutableStateOf(initialValue) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = title) },
    text = {
      OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Item title") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
      )
    },
    confirmButton = {
      TextButton(
        onClick = { onConfirm(text) },
        enabled = text.isNotBlank(),
      ) {
        Text("Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    },
  )
}
