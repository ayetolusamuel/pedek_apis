package com.pedektech.pedek_apis.pedek_catering.models

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "products", schema = "pedek_catering_database")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "sku_number", unique = true, nullable = false)
    var sku: String? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val category: String,

    @Column(nullable = true)
    val description: String? = null,

    @Column(nullable = true)
    val recipeVideoUrl: String? = null,

    @Column(nullable = true)
    val brand: String? = null,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    val images: MutableSet<ProductImage> = mutableSetOf(),

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    val priceTiers: MutableSet<PriceTier> = mutableSetOf(),
//
//    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
//    @JsonManagedReference
//    val images: MutableList<ProductImage> = mutableListOf(),
//
//    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
//    @JsonManagedReference
//    val priceTiers: MutableList<PriceTier> = mutableListOf(),

    @Column(nullable = true)
    val ingredient: String? = null,

//    @Column(nullable = true)
//    val weightOrSize: String? = null,

    @Column(nullable = true)
    val storageInstructions: String? = null,

    @Column(nullable = true)
    val expiryDate: LocalDate? = null,

    @Column(nullable = true)
    val nutritionalInfo: String? = null,

    @Column(nullable = true)
    val allergens: String? = null,

    @Column(nullable = false)
    val availableStock: Int = 0,

    @Column(nullable = true)
    val discount: Double? = null,

    @Column(nullable = true)
    val thumbnail: String? = null,

    @Column(nullable = true)
    val largeImage: String? = null,
){
        override fun equals(other: Any?): Boolean =
            this === other || (other is Product && id != null && id == other.id)

        override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String {
        return "Product(id=$id, sku=$sku, name=$name, category=$category, priceTiers=${priceTiers.size}, images=${images.size})"
    }
}


@Entity
data class ProductImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val url: String,   // image URL from Firebase storage

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    val product: Product? = null
){
    override fun toString(): String {
        return "ProductImage(id=$id, url=$url)"
    }
}

fun Product.toResponse(): ProductResponse {
    return ProductResponse(
        id = this.id,
        sku = this.sku,
        name = this.name,
        category = this.category,
        description = this.description,
        brand = this.brand,
        availableStock = this.availableStock,
        discount = this.discount,
        recipeVideoUrl = this.recipeVideoUrl,
        storageInstructions = this.storageInstructions,
        nutritionalInfo = this.nutritionalInfo,
        ingredient = this.ingredient,
        expiryDate = this.expiryDate,
        images = this.images.map { it.url }, // ✅ only URLs, no recursion
        priceTiers = this.priceTiers.map {
            PriceTierResponse(
                description = it.description,
                minQty = it.minQty,
                maxQty = it.maxQty,
                price = it.price
            )
        }
    )
}


data class ProductResponse(
    val id: Long?,
    val sku: String?,
    val name: String,
    val category: String,
    val description: String?,
    val brand: String?,
    val availableStock: Int,
    val discount: Double?,
    val recipeVideoUrl: String?,
    val storageInstructions: String?,
    val nutritionalInfo: String?,
    val ingredient: String?,
    val expiryDate: LocalDate?,
    val images: List<String>,
    val priceTiers: List<PriceTierResponse>
)

data class PriceTierResponse(
    val description: String,
    val minQty: Int,
    val maxQty: Int?,
    val price: Double
)


data class ProductRequest(
    var sku: String,
    val name: String,
    val category: String,
    val description: String? = null,
    val brand: String? = null,
    val availableStock: Int = 0,
    val discount: Double? = null,
    val recipeVideoUrl: String? = null,
    val thumbnail: String? = null,
    val largeImage: String? = null,
    val storageInstructions: String? = null,
    val nutritionalInfo: String? = null,
    val ingredient: String? = null,
    val expiryDate: LocalDate?= null,
    val priceTiers: List<PriceTierRequest>?= emptyList(),
    val images: List<String>? = emptyList()
)



data class PriceTierRequest(
    val description: String,
    val minQty: Int,
    val maxQty: Int?,
    val price: Double
)




@Entity
data class PriceTier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val description: String,
    val minQty: Int,
    val maxQty: Int?,
    val price: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    val product: Product? = null
)





data class CampaignResponse(
    val status: Boolean,
    val message: String,
    val bannerImage: String,
    val products: List<Product>,
    val isActive: Boolean
)

data class ActiveCampaignsResponse(
    val status: Boolean,
    val message: String,
    val isActive: Boolean,
    val campaigns: List<CampaignWithProducts>
)

data class CampaignWithProducts(
    val name: String,
    val bannerImage: String,
//    val products: List<ProductDTO>
    val products: List<Product>
)

