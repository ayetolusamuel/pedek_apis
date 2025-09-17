package com.pedektech.pedek_apis.gnosis.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "emotions")
data class Emotion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val type: String = "",  // default value prevents JSON parse error

    @Column(nullable = false)
    val intensity: Int = 0, // default

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)


data class EmotionRequest(
    val type: String,
    val intensity: Int
)