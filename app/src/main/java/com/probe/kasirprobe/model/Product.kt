package com.probe.kasirprobe.model

data class Product(
    val id: Int = 0,
    val name: String,
    val price: Double,
    val cost: Double,
    val stock: Int,
    val category: String,
    val imageUrl: String = "",
    val barcode: Barcode? = null
)
