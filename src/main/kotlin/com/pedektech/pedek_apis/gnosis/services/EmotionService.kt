package com.pedektech.pedek_apis.gnosis.services

import com.pedektech.pedek_apis.gnosis.models.Emotion
import com.pedektech.pedek_apis.gnosis.repositories.EmotionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EmotionService(
    private val emotionRepository: EmotionRepository
) {

    fun saveEmotion(emotion: Emotion): Emotion {
        return emotionRepository.save(emotion)
    }

    @Transactional(readOnly = true)
    fun getAllEmotions(): List<Emotion> {
        return emotionRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun getEmotionById(id: Long): Emotion? {
        return emotionRepository.findById(id).orElse(null)
    }

    @Transactional(readOnly = true)
    fun findByType(type: String): List<Emotion> {
        return emotionRepository.findByType(type)
    }

    fun deleteEmotion(id: Long) {
        emotionRepository.deleteById(id)
    }
}