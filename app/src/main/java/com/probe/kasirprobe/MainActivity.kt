package com.probe.kasirprobe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.probe.kasirprobe.ui.DashboardScreen
import com.probe.kasirprobe.ui.theme.KasirProbeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KasirProbeTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var currentScreen by remember { mutableStateOf("home") }

                    when (currentScreen) {
                        "home" -> DashboardScreen(
                            onTambahProduk = { currentScreen = "tambah" },
                            onLihatProduk = { currentScreen = "daftar" }
                        )
                        "tambah" -> com.probe.kasirprobe.ui.TambahProdukScreen { currentScreen = "home" }
                        "daftar" -> com.probe.kasirprobe.ui.DaftarProdukScreen { currentScreen = "home" }
                    }
                }
            }
        }
    }
}
