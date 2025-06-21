package com.probe.kasirprobe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(onTambahProduk: () -> Unit, onLihatProduk: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onTambahProduk) {
            Text("Tambah Produk")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onLihatProduk) {
            Text("Lihat Daftar Produk")
        }
    }
}
