package com.probe.kasirprobe.model

data class Barcode(
    val code: String,
    val type: String = "EAN",
    val createdAt: Long = System.currentTimeMillis()
)