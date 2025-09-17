package com.pedektech.pedek_apis.pedek_catering.controllers

import com.pedektech.pedek_apis.exceptions.DuplicateProductException
import com.pedektech.pedek_apis.pedek_catering.models.Product
import com.pedektech.pedek_apis.pedek_catering.models.ProductRequest
import com.pedektech.pedek_apis.pedek_catering.models.ProductResponse
import com.pedektech.pedek_apis.pedek_catering.models.toResponse
import com.pedektech.pedek_apis.pedek_catering.services.CateringProductService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.random.Random

data class ApiResponse<T>(
    val status: Boolean,
    val message: String,
    val data: T? = null
)

data class PagedResponse<T>(
    val content: List<T>,
    val pageable: PageableInfo,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
    val first: Boolean,
    val numberOfElements: Int,
    val size: Int,
    val number: Int
)

data class PageableInfo(
    val sort: SortInfo,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean
)

data class SortInfo(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean
)

@RestController
@RequestMapping("/api/v1/products")
class CateringProductController(private val productService: CateringProductService) {

    // Simple paginated endpoint with page and size parameters (1-based pagination)
    @GetMapping("/all")
    fun getAllProducts(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<List<ProductResponse>>> {
        return try {
            val products = if (size == 0) {
                productService.getAllProducts()
            } else {
                val pageable = PageRequest.of(page - 1, size)
                productService.getAllProducts(pageable).content
            }

            if (products.isEmpty()) {
                return ResponseEntity(
                    ApiResponse(false, "No products found in the database", null),
                    HttpStatus.NOT_FOUND
                )
            }

            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Products retrieved successfully (Total: ${products.size})",
                    data = products.map { it.toResponse() }
                )
            )
        } catch (e: Exception) {
            ResponseEntity(
                ApiResponse(false, "Error retrieving products: ${e.message}", null),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }


    // New paginated endpoint
    @GetMapping
    fun getAllProductsPaginated(
        @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<ApiResponse<PagedResponse<Product>>> {
        val productsPage = productService.getAllProducts(pageable)

        if (productsPage.isEmpty) {
            return ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "No products found",
                    data = null
                ),
                HttpStatus.NOT_FOUND
            )
        }

        val pagedResponse = PagedResponse(
            content = productsPage.content,
            pageable = PageableInfo(
                sort = SortInfo(
                    sorted = productsPage.pageable.sort.isSorted,
                    unsorted = productsPage.pageable.sort.isUnsorted,
                    empty = productsPage.pageable.sort.isEmpty
                ),
                pageNumber = productsPage.pageable.pageNumber,
                pageSize = productsPage.pageable.pageSize,
                offset = productsPage.pageable.offset,
                paged = productsPage.pageable.isPaged,
                unpaged = productsPage.pageable.isUnpaged
            ),
            totalElements = productsPage.totalElements,
            totalPages = productsPage.totalPages,
            last = productsPage.isLast,
            first = productsPage.isFirst,
            numberOfElements = productsPage.numberOfElements,
            size = productsPage.size,
            number = productsPage.number
        )

        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Products retrieved successfully",
                data = pagedResponse
            )
        )
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ApiResponse<Product>> {
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
    fun getProductBySku(@PathVariable sku: String): ResponseEntity<ApiResponse<Product>> {
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

    // Simple paginated favourites endpoint (1-based pagination)
    @GetMapping("/favourites/all")
    fun getAllFavouriteProducts(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sort: String = "id",
        @RequestParam(defaultValue = "asc") direction: String = "asc"
    ): ResponseEntity<ApiResponse<List<Product>>> {
        return try {
            // Convert to 0-based pagination for Spring Data
            val zeroBasedPage = if (page <= 0) 0 else page - 1

            val sortDirection = if (direction.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
            val pageable = PageRequest.of(zeroBasedPage, size, Sort.by(sortDirection, sort))
            val favouriteProductsPage = productService.getAllFavouriteProducts(pageable)

            // Check if page is beyond available pages
            if (zeroBasedPage >= favouriteProductsPage.totalPages && favouriteProductsPage.totalPages > 0) {
                return ResponseEntity(
                    ApiResponse(
                        status = false,
                        message = "Page $page not found. Total pages available: ${favouriteProductsPage.totalPages} (1-${favouriteProductsPage.totalPages})",
                        data = null
                    ),
                    HttpStatus.NOT_FOUND
                )
            }

            // Check if no data at all
            if (favouriteProductsPage.totalElements == 0L) {
                return ResponseEntity(
                    ApiResponse(
                        status = false,
                        message = "No favourite products found",
                        data = null
                    ),
                    HttpStatus.NOT_FOUND
                )
            }

            // Success response (show 1-based page numbers to user)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Favourite products retrieved successfully (Page $page of ${favouriteProductsPage.totalPages}, Total: ${favouriteProductsPage.totalElements})",
                    data = favouriteProductsPage.content
                )
            )
        } catch (e: Exception) {
            ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "Error retrieving favourite products: ${e.message}",
                    data = null
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }

    // New paginated favourites endpoint
    @GetMapping("/favourites")
    fun getAllFavouriteProductsPaginated(
        @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<ApiResponse<PagedResponse<Product>>> {
        val favouriteProductsPage = productService.getAllFavouriteProducts(pageable)

        if (favouriteProductsPage.isEmpty) {
            return ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "No favourite products found",
                    data = null
                ),
                HttpStatus.NOT_FOUND
            )
        }

        val pagedResponse = PagedResponse(
            content = favouriteProductsPage.content,
            pageable = PageableInfo(
                sort = SortInfo(
                    sorted = favouriteProductsPage.pageable.sort.isSorted,
                    unsorted = favouriteProductsPage.pageable.sort.isUnsorted,
                    empty = favouriteProductsPage.pageable.sort.isEmpty
                ),
                pageNumber = favouriteProductsPage.pageable.pageNumber,
                pageSize = favouriteProductsPage.pageable.pageSize,
                offset = favouriteProductsPage.pageable.offset,
                paged = favouriteProductsPage.pageable.isPaged,
                unpaged = favouriteProductsPage.pageable.isUnpaged
            ),
            totalElements = favouriteProductsPage.totalElements,
            totalPages = favouriteProductsPage.totalPages,
            last = favouriteProductsPage.isLast,
            first = favouriteProductsPage.isFirst,
            numberOfElements = favouriteProductsPage.numberOfElements,
            size = favouriteProductsPage.size,
            number = favouriteProductsPage.number
        )

        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Favourite products retrieved successfully",
                data = pagedResponse
            )
        )
    }

    // New endpoint for favourites by device MAC address with pagination
    @GetMapping("/favourites/device/{deviceMacAddress}")
    fun getFavouriteProductsByDevice(
        @PathVariable deviceMacAddress: String,
        @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<ApiResponse<PagedResponse<Product>>> {
        val favouriteProductsPage = productService.getFavouriteProductsByDevice(deviceMacAddress, pageable)

        if (favouriteProductsPage.isEmpty) {
            return ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "No favourite products found for this device",
                    data = null
                ),
                HttpStatus.NOT_FOUND
            )
        }

        val pagedResponse = PagedResponse(
            content = favouriteProductsPage.content,
            pageable = PageableInfo(
                sort = SortInfo(
                    sorted = favouriteProductsPage.pageable.sort.isSorted,
                    unsorted = favouriteProductsPage.pageable.sort.isUnsorted,
                    empty = favouriteProductsPage.pageable.sort.isEmpty
                ),
                pageNumber = favouriteProductsPage.pageable.pageNumber,
                pageSize = favouriteProductsPage.pageable.pageSize,
                offset = favouriteProductsPage.pageable.offset,
                paged = favouriteProductsPage.pageable.isPaged,
                unpaged = favouriteProductsPage.pageable.isUnpaged
            ),
            totalElements = favouriteProductsPage.totalElements,
            totalPages = favouriteProductsPage.totalPages,
            last = favouriteProductsPage.isLast,
            first = favouriteProductsPage.isFirst,
            numberOfElements = favouriteProductsPage.numberOfElements,
            size = favouriteProductsPage.size,
            number = favouriteProductsPage.number
        )

        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Favourite products retrieved successfully",
                data = pagedResponse
            )
        )
    }

    // Campaign products with pagination
    @GetMapping("/campaigns")
    fun getCampaignProducts(
        @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<ApiResponse<PagedResponse<Product>>> {
        val campaignProductsPage = productService.getCampaignProducts(pageable)

        if (campaignProductsPage.isEmpty) {
            return ResponseEntity(
                ApiResponse(
                    status = false,
                    message = "No campaign products found",
                    data = null
                ),
                HttpStatus.NOT_FOUND
            )
        }

        val pagedResponse = PagedResponse(
            content = campaignProductsPage.content,
            pageable = PageableInfo(
                sort = SortInfo(
                    sorted = campaignProductsPage.pageable.sort.isSorted,
                    unsorted = campaignProductsPage.pageable.sort.isUnsorted,
                    empty = campaignProductsPage.pageable.sort.isEmpty
                ),
                pageNumber = campaignProductsPage.pageable.pageNumber,
                pageSize = campaignProductsPage.pageable.pageSize,
                offset = campaignProductsPage.pageable.offset,
                paged = campaignProductsPage.pageable.isPaged,
                unpaged = campaignProductsPage.pageable.isUnpaged
            ),
            totalElements = campaignProductsPage.totalElements,
            totalPages = campaignProductsPage.totalPages,
            last = campaignProductsPage.isLast,
            first = campaignProductsPage.isFirst,
            numberOfElements = campaignProductsPage.numberOfElements,
            size = campaignProductsPage.size,
            number = campaignProductsPage.number
        )

        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Campaign products retrieved successfully",
                data = pagedResponse
            )
        )
    }

    @PostMapping
    fun createProduct(@RequestBody product: ProductRequest): ResponseEntity<ApiResponse<Product>> {
        return try {
            product.sku = "SKU" + Random.nextInt(10000000)
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
        @RequestBody updatedProduct: Product
    ): ResponseEntity<ApiResponse<Product>> {
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