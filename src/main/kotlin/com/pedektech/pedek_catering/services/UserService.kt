//package com.pedektech.pedek_catering.services
//





//import com.pedektech.pedek_catering.models.User
//import com.pedektech.pedek_catering.repositories.CateringProductRepository
//import com.pedektech.pedek_catering.repositories.FavouriteRepository
//import com.pedektech.pedek_catering.repositories.UserRepository
//import org.springframework.stereotype.Service
//
//@Service
//class UserService(
//    private val userRepository: UserRepository
//) {
//
//    /*
//    POST /auth/register – Register a new user (customer or staff)
//
//
//POST /auth/login – Authenticate user and issue JWT
//
//
//GET /auth/profile – Get current user profile (requires auth)
//
//
//PUT /auth/profile – Update profile info
//
//
//GET /admin/users – List all users (admin only)
//
//
//GET /admin/users/{userId} – Get user details (admin)
//
//
//PUT /admin/users/{userId}/role – Update user role (admin)
//     */
//
//    fun registerUser(user: User){
//        userRepository.save(user)
//    }
//    fun loginUser(){}
//    fun getUserDetails(){}
//    fun getUserProfile(){}
//    fun updateUserProfile(){}
//    fun getAllUserByAdmin(){}
//    fun updateUserDetailsByAdmin(){}
//    fun updateUserDetailsByUser(){}
//
//}