package com.tloske.overtimetracker.composables


import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tloske.overtimetracker.R
import com.tloske.overtimetracker.data.HolidayData
import com.tloske.overtimetracker.viewmodels.HolidayViewModel
import java.time.Clock
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHolidaySheetContent(viewModel: HolidayViewModel) {


    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = Clock.systemDefaultZone().millis()
    )

    val endDatePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = Clock.systemDefaultZone().millis()
    )

    var startDate by remember { mutableLongStateOf(startDatePickerState.selectedDateMillis ?: 0L) }
    var endDate by remember { mutableLongStateOf(endDatePickerState.selectedDateMillis ?: 0L) }

    var days by remember {
        mutableIntStateOf(
            calculateDays(
                endDate = endDate,
                startDate = startDate
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        ClickableTextField(
            label = stringResource(id = R.string.start),
            value = DateFormat.getPatternInstance(DateFormat.YEAR_MONTH_DAY)
                .format(startDate)
        ) {
            showStartDatePicker = true
        }

        ClickableTextField(
            label = stringResource(id = R.string.end),
            value = DateFormat.getPatternInstance(DateFormat.YEAR_MONTH_DAY)
                .format(endDate)
        ) {
            showEndDatePicker = true
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                textAlign = TextAlign.Center,

                text = "${
                    if (days == 1) stringResource(id = R.string.daysSingular) else stringResource(
                        id = R.string.days
                    )
                }: $days"
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = {
                if (days > 0f && startDate != 0L && endDate != 0L) {
                    viewModel.addHoliday(
                        HolidayData(
                            startDate = startDate,
                            endDate = endDate,
                            days = days,
                        )
                    )
                }
            }) {
            Text(text = stringResource(id = R.string.confirm))
        }

        if (showStartDatePicker) {
            CustomDatePickerDialog(
                onCancel = { showStartDatePicker = false },
                onConfirm = {
                    startDate = startDatePickerState.selectedDateMillis ?: 0L
                    days = calculateDays(endDate, startDate)

                    showStartDatePicker = false
                },
                datePickerState = startDatePickerState
            )
        }

        if (showEndDatePicker) {
            CustomDatePickerDialog(
                onCancel = { showEndDatePicker = false },
                onConfirm = {
                    endDate = endDatePickerState.selectedDateMillis ?: 0L
                    days = calculateDays(endDate, startDate)

                    showEndDatePicker = false
                },
                datePickerState = endDatePickerState
            )
        }
    }
}

private fun calculateDays(endDate: Long, startDate: Long): Int {
    var days = 0
    if (endDate >= startDate) {
        val sd = Calendar.getInstance()
        sd.time = Date(startDate)
        val ed = Calendar.getInstance()
        ed.time = Date(endDate)

        while (sd <= ed) {
            if (!sd.isWeekend) {
                days++
            }
            sd.add(Calendar.DATE, 1)
        }
    } else {
        days = 0
    }

    return days
}