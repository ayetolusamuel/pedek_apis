package com.pedektech.pedek_catering.services

import com.pedektech.pedek_catering.exceptions.DuplicateProductException
import com.pedektech.pedek_catering.models.CateringProduct
import com.pedektech.pedek_catering.repositories.CateringProductRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CateringProductService(private val productRepository: CateringProductRepository) {

    fun getAllProducts(): List<CateringProduct> = productRepository.findAll()

    fun getProductById(id: Long): Optional<CateringProduct> = productRepository.findById(id)

    fun getProductBySku(sku: String): CateringProduct? = productRepository.findBySku(sku)

    fun createProduct(product: CateringProduct): CateringProduct {
        // Check for duplicate SKU
        if (productRepository.existsBySku(product.sku?:"")) {
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

    fun deleteProduct(id: Long): Boolean {
        return if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
