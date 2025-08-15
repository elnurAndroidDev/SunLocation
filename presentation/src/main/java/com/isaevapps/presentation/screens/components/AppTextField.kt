package com.isaevapps.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isaevapps.presentation.ui.theme.PrimaryTextColor
import com.isaevapps.presentation.ui.theme.SecondaryTextColor
import com.isaevapps.presentation.ui.theme.SunLocationTheme

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    onClick: () -> Unit = {},
    placeholderText: String = "",
    label: String = "",
    errorText: String? = null,
    readOnly: Boolean = false
) {
    Box(modifier = modifier) {
        Column {
            TextField(
                value = value,
                onValueChange = { if (!readOnly) onValueChange(it) },
                readOnly = readOnly,
                isError = errorText != null,
                placeholder = { Text(placeholderText) },
                label = { Text(label, color = MaterialTheme.colorScheme.onSurface) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (errorText != null) Color.Red else MaterialTheme.colorScheme.onSurface,
                        RoundedCornerShape(12.dp)
                    )
            )
            if (errorText != null) {
                Text(errorText, color = Color.Red, fontSize = 12.sp)
            }
        }
        if (readOnly) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick() }
            )
        }
    }
}

@Preview(showSystemUi = false)
@Composable
private fun AppTextFieldPreview() {
    SunLocationTheme {
        AppTextField(
            label = "Coordinates",
            value = "41°19'49.8\"N 69°12'58.8\"E",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            errorText = "Input must contain exactly two values: lat,lon"
        )
    }
}