package com.pedektech.pedek_catering.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity(name = "favourites")
data class Favourites(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "device_mac_address", nullable = false)
    var deviceMacAddress: String,

    @Column(name = "sku", nullable = false)
    val sku: String,

)
