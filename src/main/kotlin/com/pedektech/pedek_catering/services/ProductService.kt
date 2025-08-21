package com.pedektech.pedek_catering.services

import com.pedektech.pedek_catering.exceptions.DuplicateProductException
import com.pedektech.pedek_catering.models.CampaignResponse
import com.pedektech.pedek_catering.models.CateringProduct
import com.pedektech.pedek_catering.models.Favourites
import com.pedektech.pedek_catering.repositories.CateringProductRepository
import com.pedektech.pedek_catering.repositories.FavouriteRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class CateringProductService(
    private val productRepository: CateringProductRepository,
    private val favouriteRepository: FavouriteRepository
) {

    // Original methods (kept for backward compatibility)
    fun getAllProducts(): List<CateringProduct> = productRepository.findAll()

    // New paginated method
    fun getAllProducts(pageable: Pageable): Page<CateringProduct> = productRepository.findAll(pageable)

    fun getProductById(id: Long): Optional<CateringProduct> = productRepository.findById(id)

    fun getProductBySku(sku: String): CateringProduct? = productRepository.findBySku(sku)

    fun createProduct(product: CateringProduct): CateringProduct {
        // Check for duplicate SKU
        if (productRepository.existsBySku(product.sku ?: "")) {
            throw DuplicateProductException("A product with SKU '${product.sku}' already exists.")
        }
        return productRepository.save(product)
    }

    fun updateProduct(id: Long, updatedProduct: CateringProduct): CateringProduct? {
        return if (productRepository.existsById(id)) {
            val existingProduct = updatedProduct.copy(id = id)
            productRepository.save(existingProduct)
        } else {
            null
        }
    }

    // Original method (kept for backward compatibility)
    fun getAllFavouriteProducts(): List<CateringProduct> {
        val favourites: List<Favourites> = favouriteRepository.findAll()

        // Map each favourite's SKU to the corresponding CateringProduct
        return favourites.mapNotNull { favourite ->
            productRepository.findBySku(favourite.sku) // Find the product by SKU
        }
    }

    // New paginated method for favourite products
    fun getAllFavouriteProducts(pageable: Pageable): Page<CateringProduct> {
        val favouritesPage: Page<Favourites> = favouriteRepository.findAll(pageable)

        // Convert Page<Favourites> to Page<CateringProduct>
        return favouritesPage.map { favourite ->
            productRepository.findBySku(favourite.sku)
        }.map { it!! } // Note: This assumes all favourite SKUs exist in products
    }

    // New method to get favourites by device MAC address with pagination
    fun getFavouriteProductsByDevice(deviceMacAddress: String, pageable: Pageable): Page<CateringProduct> {
        val favouritesPage: Page<Favourites> = favouriteRepository.findByDeviceMacAddress(
            deviceMacAddress.lowercase(),
            pageable
        )

        return favouritesPage.map { favourite ->
            productRepository.findBySku(favourite.sku)
        }.map { it!! } // Note: This assumes all favourite SKUs exist in products
    }

    fun addAndRemoveFavourites(favourites: Favourites): ResponseEntity<Map<String, Any>> {
        // First, check if the product exists using the SKU
        val product = productRepository.findBySku(favourites.sku)

        if (product == null) {
            throw IllegalArgumentException("Product with SKU '${favourites.sku}' does not exist.")
        }

        // Check if the favorite entry already exists
        val favouriteRetrieved = favouriteRepository.findByDeviceMacAddressAndSku(
            deviceMacAddress = favourites.deviceMacAddress.lowercase(),
            sku = favourites.sku.lowercase()
        )

        val responseMessage: String
        val operation: String

        if (favouriteRetrieved != null) {
            // If it exists, remove the favorite
            favouriteRepository.delete(favouriteRetrieved)
            responseMessage = "Favourite removed successfully"
            operation = "delete"
        } else {
            // If it does not exist, save the new favorite
            favouriteRepository.save(favourites)
            responseMessage = "Favourite added successfully"
            operation = "add"
        }

        // Create a JSON response with an operation field
        val response = mapOf(
            "status" to HttpStatus.OK.value(),
            "message" to responseMessage,
            "operation" to operation
        )

        // Return the response as JSON with status OK
        return ResponseEntity(response, HttpStatus.OK)
    }

    fun deleteProduct(id: Long): Boolean {
        return if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    // Campaign products with pagination
    fun getCampaignProducts(pageable: Pageable): Page<CateringProduct> {
        return productRepository.findAllByDiscountIsNotNull(pageable)
    }
}