package com.pedektech.pedek_catering.repositories
import com.pedektech.pedek_catering.models.Campaign
import org.springframework.data.jpa.repository.JpaRepository

interface CampaignRepository : JpaRepository<Campaign, Long> {
    fun findByName(name: String): Campaign?
    fun existsByName(name: String): Boolean
}
