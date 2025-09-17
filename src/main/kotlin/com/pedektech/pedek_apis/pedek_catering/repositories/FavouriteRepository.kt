package com.pedektech.pedek_apis.pedek_catering.repositories

import com.pedektech.pedek_apis.pedek_catering.models.Favourites
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface FavouriteRepository: JpaRepository<Favourites, Long> {
    fun findByDeviceMacAddressAndSku(deviceMacAddress: String, sku: String): Favourites?

    // Paginated methods
    override fun findAll(pageable: Pageable): Page<Favourites>
    fun findByDeviceMacAddress(deviceMacAddress: String, pageable: Pageable): Page<Favourites>
}