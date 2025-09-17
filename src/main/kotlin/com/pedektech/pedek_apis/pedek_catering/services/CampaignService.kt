package com.pedektech.pedek_apis.pedek_catering.services

import com.pedektech.pedek_apis.pedek_catering.models.*
import com.pedektech.pedek_apis.pedek_catering.repositories.CampaignRepository
import com.pedektech.pedek_apis.pedek_catering.repositories.CateringProductRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CampaignService(
    private val campaignRepository: CampaignRepository,
    private val productRepository: CateringProductRepository
) {
    fun getAllActiveCampaigns(): ActiveCampaignsResponse {
        val activeCampaigns = campaignRepository.findByActiveTrue()

        val campaigns = activeCampaigns.map { campaign ->
            val products = productRepository.findBySkuIn(campaign.productSkus)
               // .map { it.toDTO() }
               // .map { it.toDTO() }
            CampaignWithProducts(
                name = campaign.name,
                bannerImage = campaign.bannerImage,
                products = products
            )
        }

        return ActiveCampaignsResponse(
            status = true,
            message = "Active campaign fetched successfully.",
            isActive = activeCampaigns.isNotEmpty(),
            campaigns = campaigns
        )
    }




    fun getCampaignProducts(campaignName: String): CampaignResponse {
        return try {
            // Fetch campaign by name.
            val campaign = campaignRepository.findByName(campaignName)
                ?: throw Exception("Campaign not found")

            // Fetch products using SKUs from the campaign.
            val campaignProducts = productRepository.findBySkuIn(campaign.productSkus)

            CampaignResponse(
                status = true,
                message = "Campaign products fetched successfully.",
                bannerImage = campaign.bannerImage,
                isActive = campaign.active,
                products = campaignProducts
            )
        } catch (e: Exception) {
            println("Error fetching campaign products: ${e.message}")
            CampaignResponse(
                status = false,
                message = e.message ?: "Failed to fetch campaign products.",
                bannerImage = "https://example.com/error-banner.jpg",
                isActive = false,
                products = emptyList(),

                )
        }
    }


    fun updateCampaignStatus(campaignId: Long, isActive: Boolean): CampaignResponse {
        val campaign = campaignRepository.findById(campaignId)
            .orElseThrow { IllegalArgumentException("Campaign not found with id: $campaignId") }

        // Ensure the campaign's active status is updated correctly
        if (isActive && LocalDate.now().isBefore(campaign.startDate)) {
            throw IllegalArgumentException("Cannot activate a campaign before its start date.")
        }

        if (isActive && LocalDate.now().isAfter(campaign.endDate)) {
            throw IllegalArgumentException("Cannot activate a campaign after its end date.")
        }

        campaign.active = isActive
        campaignRepository.save(campaign)

        return CampaignResponse(
            status = true,
            message = "Campaign status updated successfully.",
            bannerImage = campaign.bannerImage,
            isActive = campaign.active,
            products = emptyList() // No products returned for status update response
        )
    }


    fun createCampaign(request: CampaignRequest): CampaignResponse {
        // Validate if campaign name already exists
        if (campaignRepository.existsByName(request.name)) {
            throw IllegalArgumentException("A campaign with this name already exists.")
        }

        // Validate SKUs exist in the product catalog
        val validSkus = productRepository.findBySkuIn(request.productSkus)
        if (validSkus.size != request.productSkus.size) {
            throw IllegalArgumentException("Some SKUs do not exist in the product catalog.")
        }

        // Ensure no active campaign already contains the same SKUs
        val activeCampaigns = campaignRepository.findByActiveTrue()
        val overlappingSkus = activeCampaigns.flatMap { it.productSkus }
            .intersect(request.productSkus.toSet())

        if (overlappingSkus.isNotEmpty()) {
            throw IllegalArgumentException("The following SKUs are already in active campaigns: $overlappingSkus")
        }

        // Create and save the new campaign
        val newCampaign = Campaign(
            name = request.name,
            bannerImage = request.bannerImage,
            startDate = request.startDate,
            endDate = request.endDate,
            productSkus = request.productSkus,
            active = false // Default inactive state on creation
        )

        campaignRepository.save(newCampaign)

        return CampaignResponse(
            status = true,
            message = "Campaign created successfully.",
            bannerImage = newCampaign.bannerImage,
            isActive = newCampaign.active,
            products = emptyList()
        )
    }

}
