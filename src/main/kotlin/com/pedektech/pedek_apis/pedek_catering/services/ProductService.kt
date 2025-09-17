package com.pedektech.pedek_apis.pedek_catering.services

import com.pedektech.pedek_apis.exceptions.DuplicateProductException
import com.pedektech.pedek_apis.pedek_catering.models.*
import com.pedektech.pedek_apis.pedek_catering.repositories.CateringProductRepository
import com.pedektech.pedek_apis.pedek_catering.repositories.FavouriteRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CateringProductService(
    private val productRepository: CateringProductRepository,
    private val favouriteRepository: FavouriteRepository
) {

    @Transactional(readOnly = true)
    fun getAllProducts(pageable: Pageable): Page<ProductResponse> {
        val products = productRepository.findAllWithRelations(pageable)
        val productResponses = products.map { it.toResponse() }
        return PageImpl(productResponses, pageable, productResponses.size.toLong())
    }

//    @Transactional(readOnly = true)
//    fun getAllProducts(pageable: Pageable): Page<ProductResponse> {
//        val products = productRepository.findAll(pageable)
//        return products.map { it.toResponse() } // map while session is still open
//    }

    @Transactional(readOnly = true)
    fun getAllProductsWithoutPagination(): List<ProductResponse> {
        return productRepository.findAll().map { it.toResponse() }
    }

//
//    @Transactional(readOnly = true)
//    fun getAllProducts(pageable: Pageable): Page<Product> {
//        return productRepository.findAll(pageable)
//    }

    // Original methods (kept for backward compatibility)
    fun getAllProducts(): List<Product> = productRepository.findAll()

    // New paginated method
   // fun getAllProducts(pageable: Pageable): Page<Product> = productRepository.findAll(pageable)

    fun getProductById(id: Long): Optional<Product> = productRepository.findById(id)

    fun getProductBySku(sku: String): Product? = productRepository.findBySku(sku)

    fun createProduct(request: ProductRequest): Product {
        // Check for duplicate SKU
        if (productRepository.existsBySku(request.sku ?: "")) {
            throw DuplicateProductException("A product with SKU '${request.sku}' already exists.")
        }

        val product = Product(
            sku = request.sku,
            name = request.name,
            category = request.category,
            description = request.description,
            brand = request.brand,
            availableStock = request.availableStock,
            discount = request.discount,
            nutritionalInfo = request.nutritionalInfo,
            storageInstructions = request.storageInstructions,
            ingredient = request.ingredient,
            recipeVideoUrl = request.recipeVideoUrl,
            expiryDate = request.expiryDate,
            thumbnail = request.thumbnail,
            largeImage = request.largeImage
        )

        // ✅ Map from request.priceTiers instead of product.priceTiers
        val tiers = request.priceTiers?.map {
            PriceTier(
                description = it.description,
                minQty = it.minQty,
                maxQty = it.maxQty,
                price = it.price,
                product = product
            )
        } ?: emptyList()

        // Images
        val imgList = request.images?.map {
            ProductImage(
                url = it,
                product = product
            )
        } ?: emptyList()

        product.images.addAll(imgList)
        product.priceTiers.addAll(tiers)

        println("Product.....\n$product")
        return productRepository.save(product)
    }


    fun updateProduct(id: Long, updatedProduct: Product): Product? {
        return if (productRepository.existsById(id)) {
            val existingProduct = updatedProduct.copy(id = id)
            productRepository.save(existingProduct)
        } else {
            null
        }
    }

    // Original method (kept for backward compatibility)
    fun getAllFavouriteProducts(): List<Product> {
        val favourites: List<Favourites> = favouriteRepository.findAll()

        // Map each favourite's SKU to the corresponding CateringProduct
        return favourites.mapNotNull { favourite ->
            productRepository.findBySku(favourite.sku) // Find the product by SKU
        }
    }

    // New paginated method for favourite products
    fun getAllFavouriteProducts(pageable: Pageable): Page<Product> {
        val favouritesPage: Page<Favourites> = favouriteRepository.findAll(pageable)

        // Convert Page<Favourites> to Page<CateringProduct>
        return favouritesPage.map { favourite ->
            productRepository.findBySku(favourite.sku)
        }.map { it!! } // Note: This assumes all favourite SKUs exist in products
    }

    // New method to get favourites by device MAC address with pagination
    fun getFavouriteProductsByDevice(deviceMacAddress: String, pageable: Pageable): Page<Product> {
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
    fun getCampaignProducts(pageable: Pageable): Page<Product> {
        return productRepository.findAllByDiscountIsNotNull(pageable)
    }
}