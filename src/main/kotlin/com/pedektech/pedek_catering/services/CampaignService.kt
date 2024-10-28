package com.pedektech.pedek_catering.services
import com.pedektech.pedek_catering.models.*
import com.pedektech.pedek_catering.repositories.CampaignRepository
import com.pedektech.pedek_catering.repositories.CateringProductRepository
import org.springframework.stereotype.Service

@Service
class CampaignService(
    private val campaignRepository: CampaignRepository,
    private val productRepository: CateringProductRepository
) {

    fun getCampaignProducts(campaignName: String): CampaignResponse {
        try {
            // Fetch campaign by name.
            val campaign = campaignRepository.findByName(campaignName)
                ?: throw Exception("Campaign not found")

            // Fetch products using SKUs from the campaign.
            val campaignProducts = productRepository.findBySkuIn(campaign.productSkus)

            // Map products to DTOs.
            val productDTOs = campaignProducts.map { product ->
                CateringProduct(
                    id = product.id!!,
                    sku = product.sku,
                    name = product.name,
                    category = product.category,
                    pricePerPiece = product.pricePerPiece,
                    availableStock = product.availableStock,
                    discount = product.discount,
                    thumbnail = product.thumbnail
                )
            }

            return CampaignResponse(
                status = true,
                message = "Campaign products fetched successfully.",
                bannerImage = campaign.bannerImage,
                products = campaignProducts
            )

        } catch (e: Exception) {
            println("Error fetching campaign products: ${e.message}")
            return CampaignResponse(
                status = false,
                message = e.message ?: "Failed to fetch campaign products.",
                bannerImage = "https://example.com/error-banner.jpg",
                products = emptyList()
            )
        }
    }
    fun createCampaign(request: CampaignRequest): CampaignResponse {
        // Check if a campaign with the same name already exists
        if (campaignRepository.existsByName(request.name)) {
            throw IllegalArgumentException("A campaign with this name already exists.")
        }

        // Validate that the provided SKUs exist in the products table
        val validSkus = productRepository.findBySkuIn(request.productSkus)
        if (validSkus.size != request.productSkus.size) {
            throw IllegalArgumentException("Some SKUs do not exist in the product catalog.")
        }

        // Check for duplicate SKUs across existing campaigns
        val existingCampaigns = campaignRepository.findAll()
        val duplicateSkus = existingCampaigns
            .flatMap { it.productSkus }
            .intersect(request.productSkus.toSet())

        if (duplicateSkus.isNotEmpty()) {
            throw IllegalArgumentException("The following SKUs are already part of another campaign: $duplicateSkus")
        }

        // Create and save the new campaign
        val newCampaign = Campaign(
            name = request.name,
            bannerImage = request.bannerImage,
            startDate = request.startDate,
            endDate = request.endDate,
            productSkus = request.productSkus
        )

        campaignRepository.save(newCampaign)

        return CampaignResponse(
            status = true,
            message = "Campaign created successfully.",
            bannerImage = newCampaign.bannerImage,
            products = emptyList() // No products returned in creation response.
        )
    }

}
