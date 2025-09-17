package com.pedektech.pedek_apis.gnosis.repositories

import com.pedektech.pedek_apis.gnosis.models.Emotion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmotionRepository : JpaRepository<Emotion, Long> {
    fun findByType(type: String): List<Emotion>
}