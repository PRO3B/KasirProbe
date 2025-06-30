package com.probe.kasirprobe.ui

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.probe.kasirprobe.viewmodel.ProdukViewModel
import com.probe.kasirprobe.viewmodel.ViewModelFactory

@Composable
fun DaftarProdukScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ProdukViewModel = viewModel(factory = ViewModelFactory(context.applicationContext as Application))
    val produkList by viewModel.produkList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Daftar Produk", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(12.dp))

        if (produkList.isEmpty()) {
            Text("Belum ada produk.")
        } else {
            LazyColumn {
                items(produkList) { produk ->
                    var showEdit by remember { mutableStateOf(false) }
                    var showDelete by remember { mutableStateOf(false) }

                    if (showEdit) {
                        EditProdukDialog(
                            produk = produk,
                            onDismiss = { showEdit = false },
                            onUpdate = {
                                viewModel.tambahProduk(it)
                                showEdit = false
                            }
                        )
                    }

                    if (showDelete) {
                        AlertDialog(
                            onDismissRequest = { showDelete = false },
                            title = { Text("Hapus Produk") },
                            text = { Text("Yakin ingin menghapus produk ini?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.hapusProduk(produk)
                                    showDelete = false
                                }) { Text("Hapus") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDelete = false }) { Text("Batal") }
                            }
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { showEdit = true },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nama: ${produk.nama}")
                            Text("Harga: Rp ${produk.harga}")
                            Text("Stok: ${produk.stok}")
                            produk.kategori?.let { Text("Kategori: $it") }
                            produk.barcode?.let { Text("Barcode: $it") }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(onClick = { showDelete = true }) {
                                Text("Hapus Produk")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Kembali")
        }
    }
}
