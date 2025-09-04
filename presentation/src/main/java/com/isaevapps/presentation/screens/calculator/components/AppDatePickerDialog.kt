package com.isaevapps.presentation.screens.components

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    modifier: Modifier = Modifier,
    initialDate: LocalDate = LocalDate.now(),
    onDateChange: (LocalDate) -> Unit = {},
    hideDialog: () -> Unit = {}
) {
    val initialDateMillis =
        initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = hideDialog,
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val newDate = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                    onDateChange(newDate) // Вызываем функцию ViewModel
                }
                hideDialog()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = hideDialog) {
                Text("Отмена")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}