package com.tloske.overtimetracker

import android.icu.text.DateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tloske.overtimetracker.data.OvertimeData
import com.tloske.overtimetracker.viewmodels.OvertimeViewModel
import java.time.Clock

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveOvertimeSheetContent(viewModel: OvertimeViewModel) {
    var hours by remember { mutableFloatStateOf(0f) }
    var hoursStr by remember { mutableStateOf("$hours") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Input,
        initialSelectedDateMillis = Clock.systemDefaultZone().millis()
    )
    var date by remember { mutableLongStateOf(datePickerState.selectedDateMillis ?: 0L) }



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            readOnly = true,
            label = { Text(text = "Date") },
            value = DateFormat.getPatternInstance(DateFormat.YEAR_MONTH_DAY)
                .format(date),
            onValueChange = { },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showDatePicker = true
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Hours") },
            value = hoursStr,
            onValueChange = {
                hoursStr = it
                hours = hoursStr.toFloatOrNull() ?: hours
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,

            text = "${
                if (hours == 1f) stringResource(id = R.string.hoursSingular) else stringResource(
                    id = R.string.hours
                )
            }: $hours"
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = {
                if (hours != 0f && date != 0L) {
                    viewModel.addOvertime(
                        OvertimeData(
                            date = date,
                            hours = -hours
                        )
                    )
                }
            }) {
            Text(text = "Add")
        }

        if (showDatePicker) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Row {
                        Button(onClick = { showDatePicker = false }) {
                            Text(text = "Cancel")
                        }
                        Button(onClick = {
                            date = datePickerState.selectedDateMillis ?: 0L
                            showDatePicker = false
                        }) {
                            Text(text = "Confirm")
                        }
                    }
                },
                text = {
                    DatePicker(state = datePickerState)
                })
        }

    }
}