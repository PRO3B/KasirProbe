package com.probe.kasirprobe.data.mapper

import com.probe.kasirprobe.data.db.ProductEntity
import com.probe.kasirprobe.model.Barcode
import com.probe.kasirprobe.model.Product

fun ProductEntity.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        price = price,
        cost = cost,
        stock = stock,
        category = category,
        imageUrl = imageUrl ?: "",
        barcode = barcode?.let { Barcode(it) }
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        price = price,
        cost = cost,
        stock = stock,
        category = category,
        imageUrl = imageUrl,
        barcode = barcode?.code
    )
}
