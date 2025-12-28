package com.luzian.adistance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * A centered dialog with a rounded container.
 *
 * @param onDismissRequest Callback invoked when the user tries to dismiss the dialog.
 * @param content Composable content to display inside the dialog
 */
@Composable
fun CenteredDialog(
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    Color.LightGray,
                    RoundedCornerShape(30.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                content = content
            )
        }
    }
}