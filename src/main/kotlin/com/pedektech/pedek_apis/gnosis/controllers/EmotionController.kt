package com.pedektech.pedek_apis.gnosis.controllers

import com.pedektech.pedek_apis.gnosis.models.Emotion
import com.pedektech.pedek_apis.gnosis.models.EmotionRequest
import com.pedektech.pedek_apis.gnosis.services.EmotionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/emotions")
class EmotionController(
    private val emotionService: EmotionService
) {

    @GetMapping
    fun getAllEmotions(): ResponseEntity<List<Emotion>> {
        val emotions = emotionService.getAllEmotions()
        return ResponseEntity.ok(emotions)
    }

    @GetMapping("/{id}")
    fun getEmotionById(@PathVariable id: Long): ResponseEntity<Emotion> {
        return emotionService.getEmotionById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createEmotion(@RequestBody request: EmotionRequest): ResponseEntity<Emotion> {
        val emotion = Emotion(
            type = request.type,
            intensity = request.intensity
        )
        val savedEmotion = emotionService.saveEmotion(emotion)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmotion)
    }

    @GetMapping("/type/{type}")
    fun getEmotionsByType(@PathVariable type: String): ResponseEntity<List<Emotion>> {
        val emotions = emotionService.findByType(type)
        return ResponseEntity.ok(emotions)
    }

    @PutMapping("/{id}")
    fun updateEmotion(
        @PathVariable id: Long,
        @RequestBody emotion: Emotion
    ): ResponseEntity<Emotion> {
        return if (emotionService.getEmotionById(id) != null) {
            val updatedEmotion = emotion.copy(id = id)
            ResponseEntity.ok(emotionService.saveEmotion(updatedEmotion))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteEmotion(@PathVariable id: Long): ResponseEntity<Void> {
        return if (emotionService.getEmotionById(id) != null) {
            emotionService.deleteEmotion(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}