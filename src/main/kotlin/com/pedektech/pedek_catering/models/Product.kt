package com.pedektech.pedek_catering.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity(name = "products")
data class CateringProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "sku_number", unique = true, nullable = false)
    var sku: String? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val category: String,

    @Column(nullable = true)
    val description: String? = null,

    @Column(nullable = true)
    val recipeVideoUrl: String? = null,

    @Column(nullable = true)
    val brand: String? = null,

    @Column(nullable = false)
    val pricePerPiece: Double,

    @Column(nullable = true)
    val pricePerPack: Double? = null,

    @Column(nullable = true)
    val pricePerCtn: Double? = null,

    @Column(nullable = true)
    val priceFor10To29Ctn: Double? = null,

    @Column(nullable = true)
    val priceFor30To49Ctn: Double? = null,

    @Column(nullable = true)
    val priceFor50AndAboveCtn: Double? = null,

    @Column(nullable = true)
    val ingredientList: String? = null,

    @Column(nullable = true)
    val weightOrSize: String? = null,

    @Column(nullable = true)
    val storageInstructions: String? = null,

    @Column(nullable = true)
    val expiryDate: LocalDate? = null,

    @Column(nullable = true)
    val nutritionalInfo: String? = null,

    @Column(nullable = true)
    val allergens: String? = null,

    @Column(nullable = false)
    val availableStock: Int = 0,

    @Column(nullable = true)
    val discount: Double? = null,

    @Column(nullable = true)
    val thumbnail: String? = null,

    @Column(nullable = true)
    val largeImage: String? = null
)
