package com.pedektech.pedek_apis.pedek_catering.controllers

import com.pedektech.pedek_apis.pedek_catering.models.ActiveCampaignsResponse
import com.pedektech.pedek_apis.pedek_catering.models.CampaignRequest
import com.pedektech.pedek_apis.pedek_catering.models.CampaignResponse
import com.pedektech.pedek_apis.pedek_catering.models.UpdateStatusRequest
import com.pedektech.pedek_apis.pedek_catering.services.CampaignService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/campaign")
class CampaignController(private val campaignService: CampaignService) {

    @GetMapping("/active")
    fun getAllActiveCampaigns(): ResponseEntity<ActiveCampaignsResponse> {
        val response = campaignService.getAllActiveCampaigns()
        return ResponseEntity.ok(response)
    }


    @GetMapping("/{campaignName}/products")
    fun getCampaignProducts(@PathVariable campaignName: String): CampaignResponse {
        return campaignService.getCampaignProducts(campaignName)
    }

    @PutMapping("/{id}/status")
    fun updateCampaignStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateStatusRequest
    ): CampaignResponse {
        return campaignService.updateCampaignStatus(id, request.active)
    }
    @PostMapping("/create")
    fun createCampaign(@RequestBody request: CampaignRequest): ResponseEntity<CampaignResponse> {
        return try {
            val response = campaignService.createCampaign(request)
            ResponseEntity(response, HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(
                CampaignResponse(
                    status = false,
                    message = e.message ?: "Invalid campaign data",
                    bannerImage = "https://example.com/error-banner.jpg",
                    isActive = false,
                    products = emptyList()
                ),
                HttpStatus.BAD_REQUEST
            )
        }
    }
}
