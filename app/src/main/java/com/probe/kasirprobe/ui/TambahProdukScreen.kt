package com.probe.kasirprobe.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.probe.kasirprobe.model.Produk
import com.probe.kasirprobe.viewmodel.ProdukViewModel
import com.probe.kasirprobe.viewmodel.ViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp

@Composable
fun TambahProdukScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ProdukViewModel = viewModel(factory = ViewModelFactory(context.applicationContext as Application))

    var nama by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }
    var stok by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Produk") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = harga, onValueChange = { harga = it }, label = { Text("Harga") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = stok, onValueChange = { stok = it }, label = { Text("Stok") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = kategori, onValueChange = { kategori = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = barcode, onValueChange = { barcode = it }, label = { Text("Barcode") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val produk = Produk(
                nama = nama,
                harga = harga.toDoubleOrNull() ?: 0.0,
                stok = stok.toIntOrNull() ?: 0,
                kategori = kategori.takeIf { it.isNotBlank() },
                barcode = barcode.takeIf { it.isNotBlank() }
            )
            viewModel.tambahProduk(produk)
            nama = ""; harga = ""; stok = ""; kategori = ""; barcode = ""
        }) {
            Text("Simpan Produk")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onBack) {
            Text("Kembali")
        }
    }
}
