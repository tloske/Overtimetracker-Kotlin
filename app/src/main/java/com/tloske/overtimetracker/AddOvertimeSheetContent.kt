package com.tloske.overtimetracker

import android.icu.text.DateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
fun AddOvertimeSheetContent(viewModel: OvertimeViewModel) {


    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Input,
        initialSelectedDateMillis = Clock.systemDefaultZone().millis()
    )
    val startTimePickerState = rememberTimePickerState(is24Hour = true, initialHour = 0)
    val endTimePickerState = rememberTimePickerState(is24Hour = true, initialHour = 1)

    var date by remember { mutableLongStateOf(datePickerState.selectedDateMillis ?: 0L) }
    var startTime by remember {
        mutableStateOf(
            "${startTimePickerState.hour}:${
                "${startTimePickerState.minute}".padStart(
                    2,
                    '0'
                )
            }"
        )
    }
    var endTime by remember {
        mutableStateOf(
            "${endTimePickerState.hour}:${
                "${endTimePickerState.minute}".padStart(
                    2,
                    '0'
                )
            }"
        )
    }

    var hours by remember { mutableFloatStateOf(1f) }
    var multiplier by remember { mutableFloatStateOf(1f) }
    var multiplierStr by remember { mutableStateOf("") }

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
                .padding(8.dp),
        )
        OutlinedTextField(
            readOnly = true,
            label = { Text(text = "Start Time") },
            value = startTime,
            onValueChange = { },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showStartTimePicker = true
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )
        OutlinedTextField(
            readOnly = true,
            singleLine = true,
            label = { Text(text = "End Time") },
            value = endTime,
            onValueChange = { },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showEndTimePicker = true
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
            label = { Text(text = "Multiplier") },
            value = multiplierStr,
            onValueChange = {
                multiplierStr = it
                multiplier = multiplierStr.toFloatOrNull() ?: multiplier
                hours = multiplier *
                        (((endTimePickerState.hour * 60 + endTimePickerState.minute) - (startTimePickerState.hour * 60 + startTimePickerState.minute)) / 60f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = "Multiplier: $multiplier")
            Text(
                textAlign = TextAlign.Center,

                text = "${
                    if (hours == 1f) stringResource(id = R.string.hoursSingular) else stringResource(
                        id = R.string.hours
                    )
                }: $hours"
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = {
                if (hours > 0f && startTime.isNotEmpty() && endTime.isNotEmpty() && date != 0L) {
                    viewModel.addOvertime(
                        OvertimeData(
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            hours = hours
                        )
                    )
                }
            }) {
            Text(text = "Add")
        }

        if (showStartTimePicker) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Row {
                        Button(onClick = { showStartTimePicker = false }) {
                            Text(text = "Cancel")
                        }
                        Button(onClick = {
                            startTime =
                                "${startTimePickerState.hour}:${
                                    "${startTimePickerState.minute}".padStart(
                                        2,
                                        '0'
                                    )
                                }"
                            hours = multiplier *
                                    (((endTimePickerState.hour * 60 + endTimePickerState.minute) - (startTimePickerState.hour * 60 + startTimePickerState.minute)) / 60f)
                            showStartTimePicker = false
                        }) {
                            Text(text = "Confirm")
                        }
                    }
                },
                text = {
                    TimePicker(state = startTimePickerState)
                })
        }

        if (showEndTimePicker) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Row {
                        Button(onClick = { showEndTimePicker = false }) {
                            Text(text = "Cancel")
                        }
                        Button(onClick = {
                            endTime =
                                "${endTimePickerState.hour}:${
                                    "${endTimePickerState.minute}".padStart(
                                        2,
                                        '0'
                                    )
                                }"
                            hours = multiplier *
                                    (((endTimePickerState.hour * 60 + endTimePickerState.minute) - (startTimePickerState.hour * 60 + startTimePickerState.minute)) / 60f)
                            showEndTimePicker = false
                        }) {
                            Text(text = "Confirm")
                        }
                    }
                },
                text = {
                    TimePicker(state = endTimePickerState)
                })
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