package com.tloske.overtimetracker.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tloske.overtimetracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    datePickerState: DatePickerState
) {
    DatePickerDialog(
        onDismissRequest = {},
        confirmButton = {
            Row {
                Button(onClick = onCancel) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onConfirm) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        }) {
        DatePicker(state = datePickerState)
    }
}