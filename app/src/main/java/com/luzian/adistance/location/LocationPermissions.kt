package com.luzian.adistance.location

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Composable that requests location permissions.
 *
 * Shows a Warning Dialog on Denial
 *
 * @param onGranted Callback invoked when the user grants location permissions.
 */
@Composable
fun RequestLocationPermissions(onGranted: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                onGranted()
                showDialog = false
            }

            else -> {
                showDialog = true
            }
        }
    }

    @Composable
    fun WarningDialog(
        onClose: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onClose,
            title = {
                Text("Permission denied")
            },
            text = {
                Text("This app needs location access to function properly. Without it, " +
                        "certain features will not work.")
            },
            confirmButton = {
                TextButton(onClick = onClose) {
                    Text("OK")
                }
            }
        )
    }

    if (showDialog) {
        WarningDialog { showDialog = false }
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
