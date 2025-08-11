package com.isaevapps.presentation.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePickerDialog(
    modifier: Modifier = Modifier,
    initialTime: LocalTime = LocalTime.now(),
    onTimeChange: (LocalTime) -> Unit = {},
    hideDialog: () -> Unit = {}
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )
    AlertDialog(
        modifier = modifier,
        onDismissRequest = hideDialog,
        confirmButton = {
            Button(onClick = {
                val newTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                onTimeChange(newTime)
                hideDialog()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = hideDialog) {
                Text("Отмена")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        },
    )
}