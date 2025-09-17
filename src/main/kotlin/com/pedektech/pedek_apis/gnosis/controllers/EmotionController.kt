//package com.pedektech.pedek_apis.gnosis.controllers
//
//
//import com.pedektech.pedek_apis.gnosis.models.Emotion
//import com.pedektech.pedek_apis.gnosis.services.EmotionService
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//@RestController
//@RequestMapping("/api/v1/emotion")
//class EmotionController(
//    private val emotionService: EmotionService
//) {
//
//    @PostMapping
//    fun saveEmotion(@RequestBody emotion: Emotion): ResponseEntity<Emotion> {
//        val savedEmotion = emotionService.saveEmotion(emotion)
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmotion)
//    }
//
//}