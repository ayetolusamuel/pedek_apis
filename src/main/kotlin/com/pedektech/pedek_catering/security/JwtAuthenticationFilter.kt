//package com.pedektech.pedek_catering.security
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.pedektech.pedek_catering.util.JwtUtil
//import io.jsonwebtoken.ExpiredJwtException
//import jakarta.servlet.FilterChain
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.context.SecurityContextHolder
//import org.springframework.security.core.userdetails.User
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
//import org.springframework.stereotype.Component
//import org.springframework.web.filter.OncePerRequestFilter
//
//
//@Component
//class JwtAuthenticationFilter(
//    private val jwtUtil: JwtUtil,
//    private val userDetailsService: UserDetailsService
//) : OncePerRequestFilter() {
//
//    override fun doFilterInternal(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        filterChain: FilterChain
//    ) {
//        val authorizationHeader = request.getHeader("Authorization")
//
//        try {
//            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//                val token = authorizationHeader.substring(7)
//                val username = jwtUtil.getUsernameFromToken(token)
//
//                if (username.isNotBlank() && SecurityContextHolder.getContext().authentication == null) {
//                    val userDetails = userDetailsService.loadUserByUsername(username)
//
//                    if (jwtUtil.validateToken(token, userDetails as User)) {
//                        val authToken = UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.authorities
//                        )
//                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
//                        SecurityContextHolder.getContext().authentication = authToken
//                    }
//                }
//            }
//        } catch (ex: ExpiredJwtException) {
//            // Handle expired JWT token
//            val errorResponse = mapOf(
//                "message" to "Your session has expired. Please log in again.",
//                "status" to false
//            )
//            response.status = HttpServletResponse.SC_UNAUTHORIZED
//            response.contentType = "application/json"
//            response.characterEncoding = "UTF-8"
//            response.writer.write(ObjectMapper().writeValueAsString(errorResponse))
//            return
//        } catch (ex: Exception) {
//            // Handle other exceptions
//            val errorResponse = mapOf(
//                "message" to "An error occurred while processing the token.",
//                "status" to false
//            )
//            response.status = HttpServletResponse.SC_BAD_REQUEST
//            response.contentType = "application/json"
//            response.characterEncoding = "UTF-8"
//            response.writer.write(ObjectMapper().writeValueAsString(errorResponse))
//            return
//        }
//
//        filterChain.doFilter(request, response)
//    }
//}
