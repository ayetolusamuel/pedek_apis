package com.pedektech.pedek_catering.repositories

import com.pedektech.pedek_catering.models.Product
import com.pedektech.pedek_catering.models.Favourites
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CateringProductRepository: JpaRepository<Product, Long> {
    fun findBySku(sku: String): Product?
    fun existsBySku(sku: String): Boolean
    fun findBySkuIn(skus: List<String>): List<Product>
    fun findAllByDiscountIsNotNull(): List<Product> // Fetch products with discounts (if it's part of the campaign)

    // Paginated methods
    override fun findAll(pageable: Pageable): Page<Product>
    fun findAllByDiscountIsNotNull(pageable: Pageable): Page<Product>
}

interface FavouriteRepository: JpaRepository<Favourites, Long> {
    fun findByDeviceMacAddressAndSku(deviceMacAddress: String, sku: String): Favourites?

    // Paginated methods
    override fun findAll(pageable: Pageable): Page<Favourites>
    fun findByDeviceMacAddress(deviceMacAddress: String, pageable: Pageable): Page<Favourites>
}