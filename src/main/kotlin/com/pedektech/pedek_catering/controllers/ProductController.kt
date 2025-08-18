package com.pedektech.pedek_catering.controllers

import com.pedektech.pedek_catering.exceptions.DuplicateProductException
import com.pedektech.pedek_catering.models.CateringProduct
import com.pedektech.pedek_catering.services.CateringProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.random.Random

data class ApiResponse<T>(
    val status: Boolean,
    val message: String,
    val data: T? = null
)

@RestController
@RequestMapping("/api/v1/products")
class CateringProductController(private val productService: CateringProductService) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<ApiResponse<List<CateringProduct>>> {
        val products = productService.getAllProducts()
        return if (products.isEmpty()) {
            ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "No products found",
                    data = null
                ),
                HttpStatus.NOT_FOUND
            )
        } else {
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Products retrieved successfully",
                    data = products
                )
            )
        }
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ApiResponse<CateringProduct>> {
        val product = productService.getProductById(id)
        return if (product.isPresent) {
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Product retrieved successfully",
                    data = product.get()
                )
            )
        } else {
            ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "Product not found",
                    data = null
                ),
                HttpStatus.NOT_FOUND
            )
        }
    }

    @GetMapping("/sku/{sku}")
    fun getProductBySku(@PathVariable sku: String): ResponseEntity<ApiResponse<CateringProduct>> {
        val product = productService.getProductBySku(sku)
        return if (product != null) {
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Product retrieved successfully",
                    data = product
                )
            )
        } else {
            ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "Product not found",
                    data = null
                ),
                HttpStatus.NOT_FOUND
            )
        }
    }

    @PostMapping
    fun createProduct(@RequestBody product: CateringProduct): ResponseEntity<ApiResponse<CateringProduct>> {
        return try {
            product.sku = "SKU"+ Random.nextInt(10000000)
            val newProduct = productService.createProduct(product)

            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse(
                    status = true,
                    message = "Product created successfully",
                    data = newProduct
                )
            )
        } catch (e: DuplicateProductException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse(
                    status = false,
                    message = e.message ?: "Duplicate entry error",
                    data = null
                )
            )
        }
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody updatedProduct: CateringProduct
    ): ResponseEntity<ApiResponse<CateringProduct>> {
        val product = productService.updateProduct(id, updatedProduct)
        return if (product != null) {
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Product updated successfully",
                    data = product
                )
            )
        } else {
            ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "Product not found",
                    data = null
                ),
                HttpStatus.NOT_FOUND
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<ApiResponse<Void>> {
        return if (productService.deleteProduct(id)) {
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Product deleted successfully",
                    data = null
                )
            )
        } else {
            ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "Product not found",
                    data = null
                ),
                HttpStatus.NOT_FOUND
            )
        }
    }

}
