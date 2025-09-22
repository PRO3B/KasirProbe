package com.probe.kasirprobe.ui

import androidx.compose.ui.tooling.preview.Preview
import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.probe.kasirprobe.model.Produk
import com.probe.kasirprobe.viewmodel.ProdukViewModel
import com.probe.kasirprobe.viewmodel.ViewModelFactory

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

@Preview(showBackground = true)
@Composable
fun TambahProdukScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(value = "Contoh Nama", onValueChange = {}, label = { Text("Nama Produk") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = "10000", onValueChange = {}, label = { Text("Harga") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = "5", onValueChange = {}, label = { Text("Stok") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = "Minuman", onValueChange = {}, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = "1234567890", onValueChange = {}, label = { Text("Barcode") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {}) {
            Text("Simpan Produk")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {}) {
            Text("Kembali")
        }
    }
}
