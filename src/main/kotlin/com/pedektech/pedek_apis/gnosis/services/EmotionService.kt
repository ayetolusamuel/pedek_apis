package com.pedektech.pedek_apis.gnosis.services


import com.pedektech.pedek_apis.gnosis.models.Emotion
import com.pedektech.pedek_apis.gnosis.repositories.EmotionRepository
import org.springframework.stereotype.Service

@Service
class EmotionService(
    private val emotionRepository: EmotionRepository
) {

    fun saveEmotion(emotion: Emotion): Emotion {
        // Optional validation (example)
        require(emotion.name.isNotBlank()) { "Emotion name cannot be blank" }

        return emotionRepository.save(emotion)
    }
}
