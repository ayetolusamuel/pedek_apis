package com.pedektech.pedek_catering.models
import jakarta.persistence.*
import java.time.LocalDate

@Entity(name = "campaigns")
data class Campaign(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val bannerImage: String,

    @Column(nullable = false)
    val startDate: LocalDate,

    @Column(nullable = false)
    val endDate: LocalDate,
    @Column(nullable = false)
    var active: Boolean = false,

    @ElementCollection
    @CollectionTable(
        name = "campaign_products",
        joinColumns = [JoinColumn(name = "campaign_id")]
    )
    @Column(name = "sku", nullable = false)
    val productSkus: List<String>
)

data class CampaignRequest(
    val name: String,
    val bannerImage: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val productSkus: List<String>
)

data class ProductDTO(
    val id: Long,
    val sku: String?,
    val name: String,
    val category: String,
    //val pricePerPiece: Double,
    val availableStock: Int,
    val discount: Double?,
    val thumbnail: String?
)


data class UpdateStatusRequest(val active: Boolean)