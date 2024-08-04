package com.tloske.overtimetracker.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ClickableTextField(label: String, value: String, onClick: () -> Unit) {
    OutlinedTextField(
        readOnly = true,
        label = { Text(text = label) },
        value = value,
        onValueChange = { },
        interactionSource = remember {
            MutableInteractionSource()
        }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    if (it is PressInteraction.Release) {
                        onClick()
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = true
    )
}
