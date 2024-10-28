package com.pedektech.pedek_catering.repositories

import com.pedektech.pedek_catering.models.CateringProduct
import com.pedektech.pedek_catering.models.Favourites
import org.springframework.data.jpa.repository.JpaRepository

interface CateringProductRepository: JpaRepository<CateringProduct, Long>{
    fun findBySku(sku: String): CateringProduct?
    fun existsBySku(sku: String): Boolean
    fun findBySkuIn(skus: List<String>): List<CateringProduct>
    fun findAllByDiscountIsNotNull(): List<CateringProduct> // Fetch products with discounts (if it's part of the campaign).
}

interface FavouriteRepository:JpaRepository<Favourites,Long>{
    fun findByDeviceMacAddressAndSku(deviceMacAddress: String, sku: String): Favourites?
}