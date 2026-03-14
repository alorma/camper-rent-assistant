package com.alorma.camperchecks.screens.addrental

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alorma.camperchecks.R
import com.alorma.camperchecks.rental.RentalProvider
import com.alorma.camperchecks.ui.components.scaffold.AppScaffold
import com.alorma.camperchecks.ui.components.topbar.NavigationIcon
import com.alorma.camperchecks.ui.components.topbar.StyledTopAppBar
import com.alorma.camperchecks.ui.theme.AppTheme
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddRentalScreen(
  onNavigateBack: () -> Unit,
  viewModel: AddRentalViewModel = koinViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.navigationSideEffects.collect { effect ->
      when (effect) {
        AddRentalNavigationSideEffect.NavigateBack -> onNavigateBack()
      }
    }
  }

  AppScaffold(
    topBar = {
      StyledTopAppBar(
        title = { Text(text = stringResource(R.string.add_rental_title)) },
        navigationIcon = { NavigationIcon() },
      )
    },
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(
        top = paddingValues.calculateTopPadding() + 16.dp,
        bottom = paddingValues.calculateBottomPadding() + 16.dp,
        start = 16.dp,
        end = 16.dp,
      ),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        MandatoryFieldsLegend()
      }

      item {
        OutlinedTextField(
          value = RentalProvider.Yescapa.displayName,
          onValueChange = {},
          modifier = Modifier.fillMaxWidth(),
          label = { Text(stringResource(R.string.add_rental_field_provider)) },
          enabled = false,
          singleLine = true,
        )
      }

      item {
        OutlinedTextField(
          value = uiState.referenceId,
          onValueChange = viewModel::onReferenceIdChange,
          modifier = Modifier.fillMaxWidth(),
          label = { RequiredLabel(stringResource(R.string.add_rental_field_reference_id)) },
          placeholder = { Text(stringResource(R.string.add_rental_field_reference_id_placeholder)) },
          singleLine = true,
          enabled = !uiState.isSaving,
        )
      }

      item {
        DateTimeField(
          label = stringResource(R.string.add_rental_field_start_datetime),
          value = uiState.startAt?.toString()?.replace("T", "  ") ?: "",
          onDateSelected = viewModel::onStartDateSelected,
          onTimeSelected = viewModel::onStartTimeSelected,
          enabled = !uiState.isSaving,
        )
      }

      item {
        DateTimeField(
          label = stringResource(R.string.add_rental_field_end_datetime),
          value = uiState.endAt?.toString()?.replace("T", "  ") ?: "",
          onDateSelected = viewModel::onEndDateSelected,
          onTimeSelected = viewModel::onEndTimeSelected,
          enabled = !uiState.isSaving,
        )
      }

      item {
        OutlinedTextField(
          value = uiState.renterName,
          onValueChange = viewModel::onRenterNameChange,
          modifier = Modifier.fillMaxWidth(),
          label = { RequiredLabel(stringResource(R.string.add_rental_field_renter_name)) },
          placeholder = { Text(stringResource(R.string.add_rental_field_renter_name_placeholder)) },
          singleLine = true,
          enabled = !uiState.isSaving,
        )
      }

      item {
        OutlinedTextField(
          value = uiState.renterPhone,
          onValueChange = viewModel::onRenterPhoneChange,
          modifier = Modifier.fillMaxWidth(),
          label = { Text(stringResource(R.string.add_rental_field_renter_phone)) },
          placeholder = { Text(stringResource(R.string.add_rental_field_renter_phone_placeholder)) },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          enabled = !uiState.isSaving,
        )
      }

      item {
        OutlinedTextField(
          value = uiState.renterNotes,
          onValueChange = viewModel::onRenterNotesChange,
          modifier = Modifier.fillMaxWidth(),
          label = { Text(stringResource(R.string.add_rental_field_renter_notes)) },
          placeholder = { Text(stringResource(R.string.add_rental_field_renter_notes_placeholder)) },
          minLines = 2,
          maxLines = 4,
          enabled = !uiState.isSaving,
        )
      }

      item {
        OutlinedTextField(
          value = uiState.notes,
          onValueChange = viewModel::onNotesChange,
          modifier = Modifier.fillMaxWidth(),
          label = { Text(stringResource(R.string.add_rental_field_notes)) },
          placeholder = { Text(stringResource(R.string.add_rental_field_notes_placeholder)) },
          minLines = 2,
          maxLines = 4,
          enabled = !uiState.isSaving,
        )
      }

      item {
        if (uiState.hasError) {
          Text(
            text = stringResource(R.string.error_generic),
            color = AppTheme.colorScheme.error,
            style = AppTheme.typography.bodySmall,
          )
          Spacer(modifier = Modifier.height(4.dp))
        }
      }

      item {
        if (uiState.isSaving) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
          ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
          }
        } else {
          Button(
            onClick = viewModel::onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isValid,
          ) {
            Text(stringResource(R.string.add_rental_button_save))
          }
        }
      }
    }
  }
}

@Composable
private fun MandatoryFieldsLegend() {
  val requiredField = stringResource(R.string.required_field)
  val text = buildAnnotatedString {
    pushStyle(SpanStyle(color = AppTheme.colorScheme.error))
    append("*")
    pop()
    append(" $requiredField")
  }
  Text(
    text = text,
    style = AppTheme.typography.bodySmall,
    color = AppTheme.colorScheme.onSurfaceVariant,
  )
}

@Composable
private fun RequiredLabel(label: String) {
  val text = buildAnnotatedString {
    append(label)
    append(" ")
    pushStyle(SpanStyle(color = AppTheme.colorScheme.error))
    append("*")
    pop()
  }
  Text(text = text)
}

@Composable
private fun DateTimeField(
  label: String,
  value: String,
  onDateSelected: (LocalDate) -> Unit,
  onTimeSelected: (LocalTime) -> Unit,
  enabled: Boolean,
) {
  var showDatePicker by rememberSaveable { mutableStateOf(false) }
  var showTimePicker by rememberSaveable { mutableStateOf(false) }

  OutlinedTextField(
    value = value,
    onValueChange = {},
    modifier = Modifier.fillMaxWidth(),
    label = { Text(label) },
    placeholder = { Text(stringResource(R.string.tap_to_select)) },
    readOnly = true,
    enabled = enabled,
    singleLine = true,
    trailingIcon = {
      Row {
        TextButton(
          onClick = { showDatePicker = true },
          enabled = enabled,
        ) { Text(stringResource(R.string.date)) }
        TextButton(
          onClick = { showTimePicker = true },
          enabled = enabled,
        ) { Text(stringResource(R.string.time)) }
      }
    },
  )

  if (showDatePicker) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            @OptIn(ExperimentalTime::class)
            datePickerState.selectedDateMillis?.let { millis ->
              val date = Instant.fromEpochMilliseconds(millis)
                .toLocalDateTime(TimeZone.UTC)
                .date
              onDateSelected(date)
            }
            showDatePicker = false
          },
        ) { Text(stringResource(R.string.ok)) }
      },
      dismissButton = {
        TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
      },
    ) {
      DatePicker(state = datePickerState)
    }
  }

  if (showTimePicker) {
    val timePickerState = rememberTimePickerState()
    AlertDialog(
      onDismissRequest = { showTimePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            onTimeSelected(LocalTime(timePickerState.hour, timePickerState.minute))
            showTimePicker = false
          },
        ) { Text(stringResource(R.string.ok)) }
      },
      dismissButton = {
        TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) }
      },
      text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          TimePicker(state = timePickerState)
        }
      },
    )
  }
}
