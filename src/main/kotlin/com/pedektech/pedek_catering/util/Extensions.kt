package com.pedektech.pedek_catering.util

import com.pedektech.pedek_catering.models.Product
import com.pedektech.pedek_catering.models.ProductDTO

fun Product.toDTO(): ProductDTO {
    return ProductDTO(
        id = this.id ?: 0,
        sku = this.sku,
        name = this.name,
        category = this.category,
       // pricePerPiece = this.pricePerPiece,
        availableStock = this.availableStock,
        discount = this.discount,
        thumbnail = this.thumbnail
    )
}