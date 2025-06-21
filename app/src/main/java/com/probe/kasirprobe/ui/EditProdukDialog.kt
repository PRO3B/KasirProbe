package com.probe.kasirprobe.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import com.probe.kasirprobe.model.Produk

@Composable
fun EditProdukDialog(
    produk: Produk,
    onDismiss: () -> Unit,
    onUpdate: (Produk) -> Unit
) {
    var nama by remember { mutableStateOf(produk.nama) }
    var harga by remember { mutableStateOf(produk.harga.toString()) }
    var stok by remember { mutableStateOf(produk.stok.toString()) }
    var kategori by remember { mutableStateOf(produk.kategori ?: "") }
    var barcode by remember { mutableStateOf(produk.barcode ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onUpdate(
                    produk.copy(
                        nama = nama,
                        harga = harga.toDoubleOrNull() ?: 0.0,
                        stok = stok.toIntOrNull() ?: 0,
                        kategori = kategori.takeIf { it.isNotBlank() },
                        barcode = barcode.takeIf { it.isNotBlank() }
                    )
                )
            }) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        title = { Text("Edit Produk") },
        text = {
            Column {
                OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = harga, onValueChange = { harga = it }, label = { Text("Harga") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = stok, onValueChange = { stok = it }, label = { Text("Stok") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = kategori, onValueChange = { kategori = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = barcode, onValueChange = { barcode = it }, label = { Text("Barcode") }, modifier = Modifier.fillMaxWidth())
            }
        }
    )
}
