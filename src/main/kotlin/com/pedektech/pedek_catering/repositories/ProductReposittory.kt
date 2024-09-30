package com.pedektech.pedek_catering.repositories

import com.pedektech.pedek_catering.models.CateringProduct
import org.springframework.data.jpa.repository.JpaRepository

interface CateringProductRepository: JpaRepository<CateringProduct, Long>{
    fun findBySku(sku: String): CateringProduct?
    fun existsBySku(sku: String): Boolean
}