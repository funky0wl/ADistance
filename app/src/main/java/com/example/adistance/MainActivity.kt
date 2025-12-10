package com.example.adistance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.adistance.ui.theme.ADistanceTheme
import org.maplibre.android.maps.Style
import org.ramani.compose.MapLibre


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ADistanceTheme {
                MapLibre(modifier = Modifier.fillMaxSize(), Style.Builder().fromUri("asset://style.json"))
            }
        }
    }
}