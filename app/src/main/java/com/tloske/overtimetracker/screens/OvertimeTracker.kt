package com.tloske.overtimetracker.screens

import android.icu.text.DateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tloske.overtimetracker.R
import com.tloske.overtimetracker.data.OvertimeData
import com.tloske.overtimetracker.viewmodels.OvertimeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OvertimeTracker(
    viewModel: OvertimeViewModel = viewModel(),
    bottomBar: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(id = R.string.overtime))
//                        Text(
//                            text = "$overtime ${stringResource(id = R.string.hoursShort)}",
//                            fontSize = 16.sp
//                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showBottomSheet = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        bottomBar = { bottomBar() },
    ) { innerPadding ->
        val overtimeList = viewModel.getOvertimeList.collectAsState(initial = listOf())
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            items(overtimeList.value) { item ->
                val dismissState = rememberSwipeToDismissBoxState(
                    positionalThreshold = { 500f },
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.deleteOvertime(item)
                        }
                        true
                    }
                )
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        val color by animateColorAsState(
                            targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent,
                            label = ""
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = color,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            border = CardDefaults.outlinedCardBorder(enabled = true)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Icon",
                                )
                            }
                        }
                    },
                    enableDismissFromStartToEnd = false,
                    enableDismissFromEndToStart = true,
                ) {
                    OvertimeListItem(overtimeData = item)
                }
            }
        }

        if (viewModel.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.showBottomSheet = false },
                sheetState = sheetState
            ) {
                OvertimeSheetContent(viewModel = viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OvertimeListItem(overtimeData: OvertimeData) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = CardDefaults.outlinedCardBorder(enabled = true)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = DateFormat.getPatternInstance(DateFormat.YEAR_MONTH_DAY)
                    .format(overtimeData.date)
            )
            if (overtimeData.startTime.isNotEmpty()) Text(text = "${overtimeData.startTime} - ${overtimeData.endTime}")
            Text(text = "${overtimeData.hours} ${stringResource(id = R.string.hoursShort)}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OvertimeSheetContent(viewModel: OvertimeViewModel) {
    var date by remember { mutableLongStateOf(0L) };
    var startTime by remember { mutableStateOf("") };
    var endTime by remember { mutableStateOf("") }
    var hours by remember { mutableFloatStateOf(1f) }
    var multiplier by remember { mutableFloatStateOf(1f) }


    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
    val startTimePickerState = rememberTimePickerState(is24Hour = true)
    val endTimePickerState = rememberTimePickerState(is24Hour = true)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            enabled = false,
            readOnly = true,
            label = { Text(text = "Date") },
            value = DateFormat.getPatternInstance(DateFormat.YEAR_MONTH_DAY)
                .format(date),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { showDatePicker = true },
        )
        OutlinedTextField(
            enabled = false,
            readOnly = true,
            label = { Text(text = "Start Time") },
            value = startTime,
            onValueChange = { startTime = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    showStartTimePicker = true
                },
            singleLine = true
        )
        OutlinedTextField(
            enabled = false,
            readOnly = true,
            singleLine = true,
            label = { Text(text = "End Time") },
            value = endTime,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { showEndTimePicker = true }
        )
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Multiplier") },
            value = "$multiplier",
            onValueChange = {
                multiplier = it.toFloatOrNull() ?: multiplier
                hours = multiplier *
                        (((endTimePickerState.hour * 60 + endTimePickerState.minute) - (startTimePickerState.hour * 60 + startTimePickerState.minute)) / 60f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Text(
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                if (hours != 0f && startTime.isNotEmpty() && endTime.isNotEmpty() && date != 0L) {
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