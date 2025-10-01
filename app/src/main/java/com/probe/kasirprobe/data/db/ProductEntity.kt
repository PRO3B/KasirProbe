package com.probe.kasirprobe.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val cost: Double,
    val stock: Int,
    val category: String,
    val imageUrl: String? = null,
    val barcode: String? = null
)