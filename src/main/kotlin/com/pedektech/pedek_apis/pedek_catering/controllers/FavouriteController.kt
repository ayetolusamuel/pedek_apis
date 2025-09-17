package com.pedektech.pedek_apis.pedek_catering.controllers

import com.pedektech.pedek_apis.pedek_catering.models.Product
import com.pedektech.pedek_apis.pedek_catering.models.Favourites
import com.pedektech.pedek_apis.pedek_catering.services.CateringProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/favourites")
class FavouritesController(private val productService: CateringProductService) {

    @PostMapping("/toggle")
    fun addOrRemoveFavourites(@RequestBody favourites: Favourites): ResponseEntity<Map<String, Any>> {
        return productService.addAndRemoveFavourites(favourites)
    }
    @GetMapping("/all")
    fun getAllFavourites(): ResponseEntity<Map<String, Any>> {
        val favourites: List<Product> = productService.getAllFavouriteProducts()
        val response = mapOf(
            "status" to HttpStatus.OK.value(),
            "message" to "Favourites retrieved successfully",
            "data" to favourites
        )
        return ResponseEntity(response, HttpStatus.OK)
    }
}