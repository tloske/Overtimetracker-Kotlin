package com.tloske.overtimetracker.screens

import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tloske.overtimetracker.R
import com.tloske.overtimetracker.composables.AddHolidaySheetContent
import com.tloske.overtimetracker.data.HolidayData
import com.tloske.overtimetracker.viewmodels.HolidayViewModel
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidayTracker(
    viewModel: HolidayViewModel = viewModel(),
    bottomBar: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val holidayList = viewModel.getHolidayList.collectAsState(initial = listOf())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    var days = 0
                    holidayList.value.forEach {
                        val sd = Calendar.getInstance()
                        val year = sd.get(Calendar.YEAR)
                        sd.time = Date(it.startDate)
                        val ed = Calendar.getInstance()
                        ed.time = Date(it.endDate)

                        while (sd <= ed) {
                            if (sd.get(Calendar.YEAR) == year && !sd.isWeekend) {
                                days++
                            }
                            sd.add(Calendar.DATE, 1)
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${stringResource(id = R.string.holiday)} ${
                                Calendar.getInstance().get(Calendar.YEAR)
                            }"
                        )
                        Text(
                            text = "$days ${
                                if (days == 1) stringResource(id = R.string.daysSingular)
                                else stringResource(id = R.string.days)
                            }",
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    viewModel.showBottomSheet = true
                }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(holidayList.value, key = { it.uid }) { item ->
                val dismissState = rememberSwipeToDismissBoxState(
                    positionalThreshold = { 500f },
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.deleteHoliday(item)
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
                    HolidayListItem(holidayData = item)
                }
            }
        }
        if (viewModel.showBottomSheet) {
            ModalBottomSheet(
                windowInsets = WindowInsets.navigationBars.union(WindowInsets.ime),
                onDismissRequest = { viewModel.showBottomSheet = false },
                sheetState = sheetState
            ) {
                AddHolidaySheetContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HolidayListItem(holidayData: HolidayData) {
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
                text =
                if (holidayData.endDate == holidayData.startDate)
                    DateFormat.getPatternInstance(DateFormat.YEAR_MONTH_DAY)
                        .format(holidayData.startDate)
                else
                    "${
                        DateFormat.getPatternInstance(DateFormat.YEAR_MONTH_DAY)
                            .format(holidayData.startDate)
                    } - ${
                        DateFormat.getPatternInstance(DateFormat.YEAR_MONTH_DAY)
                            .format(holidayData.endDate)
                    }"
            )

            Text(
                text = "${holidayData.days} ${
                    if (holidayData.days == 1) stringResource(id = R.string.daysSingular) else stringResource(
                        id = R.string.days
                    )
                }"
            )
        }
    }
}