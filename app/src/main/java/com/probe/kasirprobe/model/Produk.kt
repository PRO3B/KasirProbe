package com.probe.kasirprobe.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produk")
data class Produk(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val harga: Double,
    val stok: Int,
    val kategori: String? = null,
    val barcode: String? = null
)
