package com.alorma.camperchecks.screens.checklisttemplates

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.camperchecks.R
import com.alorma.camperchecks.checklist.ChecklistPhase
import com.alorma.camperchecks.checklist.ChecklistTemplate
import com.alorma.camperchecks.icons.AppIcons
import com.alorma.camperchecks.icons.filled.Delete
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.NavigationIcon
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar
import com.alorma.camperchecks.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChecklistTemplatesScreen(
  onNavigateBack: () -> Unit,
  viewModel: ChecklistTemplatesViewModel = koinViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.navigationSideEffects.collect { effect ->
      when (effect) {
        ChecklistTemplatesNavigationSideEffect.NavigateBack -> onNavigateBack()
      }
    }
  }

  AppScaffold(
    topBar = {
      StyledTopAppBar(
        title = { Text(text = stringResource(R.string.checklist_templates_title)) },
        navigationIcon = { NavigationIcon() },
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = viewModel::onAddItemClick,
        text = { Text(stringResource(R.string.checklist_templates_add_item)) },
        icon = {},
      )
    },
  ) { paddingValues ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(top = paddingValues.calculateTopPadding()),
    ) {
      PhaseTabRow(
        selectedPhase = uiState.selectedPhase,
        onPhaseSelected = viewModel::onPhaseSelected,
      )

      HorizontalDivider()

      TemplateItemsList(
        items = uiState.currentPhaseTemplates,
        onEditItem = viewModel::onEditItemClick,
        onDeleteItem = viewModel::onDeleteItem,
        contentPadding =
          PaddingValues(
            bottom = paddingValues.calculateBottomPadding() + 88.dp,
            start = 16.dp,
            end = 16.dp,
          ),
      )
    }
  }

  when (val dialog = uiState.dialogState) {
    ChecklistTemplateDialogState.Hidden -> Unit
    ChecklistTemplateDialogState.Adding -> {
      AddEditTemplateDialog(
        title = stringResource(R.string.checklist_templates_dialog_add_title),
        initialValue = "",
        onDismiss = viewModel::onDismissDialog,
        onConfirm = viewModel::onSaveItem,
      )
    }
    is ChecklistTemplateDialogState.Editing -> {
      AddEditTemplateDialog(
        title = stringResource(R.string.checklist_templates_dialog_edit_title),
        initialValue = dialog.template.title,
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
private fun TemplateItemsList(
  items: List<ChecklistTemplate>,
  onEditItem: (ChecklistTemplate) -> Unit,
  onDeleteItem: (ChecklistTemplate) -> Unit,
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
        TemplateItemRow(
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
    modifier =
      Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(vertical = 48.dp),
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = stringResource(R.string.checklist_templates_empty_title),
      style = AppTheme.typography.headlineSmall,
      color = AppTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = stringResource(R.string.checklist_templates_empty_subtitle),
      style = AppTheme.typography.bodyMedium,
      color = AppTheme.colorScheme.outline,
    )
  }
}

@Composable
private fun TemplateItemRow(
  item: ChecklistTemplate,
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
          contentDescription = stringResource(R.string.checklist_templates_delete_item),
        )
      }
    },
  )
}

@Composable
private fun AddEditTemplateDialog(
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
        label = { Text(stringResource(R.string.checklist_templates_dialog_item_label)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
      )
    },
    confirmButton = {
      TextButton(
        onClick = { onConfirm(text) },
        enabled = text.isNotBlank(),
      ) {
        Text(stringResource(R.string.ok))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel))
      }
    },
  )
}
