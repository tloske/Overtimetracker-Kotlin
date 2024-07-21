package com.tloske.overtimetracker.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HolidayTracker() {
    Text(text = "Holiday")
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {}

}