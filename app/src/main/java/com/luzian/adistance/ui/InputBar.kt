package com.luzian.adistance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment

/**
 * A input bar positioned at the bottom, with a send button.
 *
 * @param onSend Callback invoked when the user presses the send button.
 * @param content Composable content to display inside the row
 */
@Composable
fun InputBar(
    onSend: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.statusBars),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 25.dp, start = 15.dp, end = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            content()

            Button(
                onClick = onSend,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardReturn, "Enter")
            }
        }
    }
}

@Composable
fun RowScope.InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        shape = RoundedCornerShape(50),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        placeholder = { Text(placeholderText, color = Color.Gray) },
        textStyle = LocalTextStyle.current.copy(color = Color.Black),
        modifier = Modifier
            .weight(1f)
            .heightIn()
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(50)
            )
    )
}
