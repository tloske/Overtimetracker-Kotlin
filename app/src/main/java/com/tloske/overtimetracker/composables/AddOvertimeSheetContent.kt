package com.tloske.overtimetracker.composables

import android.icu.text.DateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
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
import com.tloske.overtimetracker.R
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
        initialDisplayMode = DisplayMode.Picker,
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
        ClickableTextField(
            label = stringResource(id = R.string.date),
            value = DateFormat.getPatternInstance(DateFormat.YEAR_MONTH_DAY)
                .format(date)
        ) {
            showDatePicker = true
        }

        ClickableTextField(label = stringResource(id = R.string.start), value = startTime) {
            showStartTimePicker = true
        }

        ClickableTextField(label = stringResource(id = R.string.end), value = endTime) {
            showEndTimePicker = true
        }

        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = stringResource(id = R.string.multiplier)) },
            value = multiplierStr,
            onValueChange = {
                multiplierStr = it
                multiplier = multiplierStr.toFloatOrNull() ?: 1f
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
            Text(text = "${stringResource(id = R.string.multiplier)}: $multiplier")
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
            Text(text = stringResource(id = R.string.confirm))
        }

        if (showStartTimePicker) {
            TimePickerDialog(
                onCancel = { showStartTimePicker = false },
                onConfirm = {
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
                },
                timePickerState = startTimePickerState
            )
        }

        if (showEndTimePicker) {
            TimePickerDialog(
                onCancel = { showEndTimePicker = false },
                onConfirm = {
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
                },
                timePickerState = endTimePickerState
            )
        }

        if (showDatePicker) {
            CustomDatePickerDialog(
                onCancel = { showDatePicker = false },
                onConfirm = {
                    date = datePickerState.selectedDateMillis ?: 0L
                    showDatePicker = false
                },
                datePickerState = datePickerState
            )
        }

    }
}