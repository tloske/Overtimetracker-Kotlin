package com.tloske.overtimetracker.screens

import android.icu.text.DateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tloske.overtimetracker.R
import com.tloske.overtimetracker.composables.AddOvertimeSheetContent
import com.tloske.overtimetracker.composables.RemoveOvertimeSheetContent
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
    val overtimeList = viewModel.getOvertimeList.collectAsState(initial = listOf())
    var hours: Float
    var showFabs by remember { mutableStateOf(false) }
    var addRemoveToggle = false

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    hours = 0f
                    overtimeList.value.forEach {
                        hours += it.hours
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(id = R.string.overtime))
                        Text(
                            text = "$hours ${stringResource(id = R.string.hoursShort)}",
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {

            if (showFabs) {
                Column {
                    FloatingActionButton(onClick = {
                        viewModel.showBottomSheet = true
                        addRemoveToggle = true
                        showFabs = !showFabs
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(onClick = {
                        viewModel.showBottomSheet = true
                        addRemoveToggle = false
                        showFabs = !showFabs
                    }) {
                        Text(text = "-", fontSize = 32.sp)
                    }
                }
            } else {
                FloatingActionButton(onClick = { showFabs = !showFabs }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Add")
                }
            }
        },
        bottomBar = { bottomBar() },
    ) { innerPadding ->
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
                windowInsets = WindowInsets.navigationBars.union(WindowInsets.ime),
                onDismissRequest = { viewModel.showBottomSheet = false },
                sheetState = sheetState
            ) {
                if (addRemoveToggle) {
                    AddOvertimeSheetContent(viewModel = viewModel)
                } else {
                    RemoveOvertimeSheetContent(viewModel = viewModel)
                }

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



