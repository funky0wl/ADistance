package com.luzian.adistance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * A bottom-left positioned sidebar.
 *
 * @param content Composable content to display inside the sidebar.
 */
@Composable
fun BoxScope.Sidebar(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 15.dp, bottom = 120.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.statusBars),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

/**
 * A circular icon button.
 *
 * @param onClick Callback invoked when the button is pressed.
 * @param icon The [ImageVector] to display inside the button.
 * @param contentDescription Optional description for accessibility.
 */
@Composable
fun IconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(56.dp),
        contentPadding = PaddingValues(0.dp),
        content = { Icon(icon, contentDescription) }
    )
}
