//package com.pedektech.ncym.security
//
//import com.pedektech.ncym.repository.UserRepository
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.core.userdetails.UsernameNotFoundException
//import org.springframework.stereotype.Service
//
//@Service
//class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
//
//    override fun loadUserByUsername(username: String): UserDetails {
//        val user = userRepository.findByUserId(username)
//            ?: throw UsernameNotFoundException("User not found with username: $username")
//
//        return user
//    }
//}
