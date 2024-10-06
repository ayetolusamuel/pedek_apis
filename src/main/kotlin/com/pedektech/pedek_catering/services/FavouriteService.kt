//package com.pedektech.pedek_catering.services
//
//import com.pedektech.pedek_catering.models.Favourites
//import com.pedektech.pedek_catering.repositories.FavouriteRepository
//import org.springframework.stereotype.Service
//
//@Service
//class FavouriteService(
//    private val favouriteRepository: FavouriteRepository) {
//
//    fun addAndRemoveFavourites(favourites: Favourites){
//        //If there productSKU and mac address does not exist save else remove
//        val favouriteRetrieved = favouriteRepository.findBydeviceMacAddressAndSku(sku = favourites.sku, deviceMacAddress = favourites.deviceMacAddress)
//        if (favouriteRetrieved != null){
//            favouriteRepository.delete(favourites)
//        }else{
//             favouriteRepository.save(favourites)
//        }
//    }
//
//}
