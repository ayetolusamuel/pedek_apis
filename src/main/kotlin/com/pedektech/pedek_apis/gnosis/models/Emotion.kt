package com.pedektech.pedek_apis.gnosis.models

import jakarta.persistence.*


@Entity()
@Table(name = "emotion", schema = "gnosis_database")
data class Emotion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val isEmergencyCalmEnabled: Boolean? = null,
)