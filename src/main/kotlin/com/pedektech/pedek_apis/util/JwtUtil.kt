package com.pedektech.pedek_apis.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil {

    private val secretKey = "your_secret_key"

    // Generate a JWT token for the given username
    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration
            .signWith(SignatureAlgorithm.HS256, secretKey.toByteArray())
            .compact()
    }

    // Validate the JWT token against the user details
    fun validateToken(token: String, userDetails: org.springframework.security.core.userdetails.User): Boolean {
        return try {
            val username = getUsernameFromToken(token)
            username == userDetails.username && !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }

    // Extract the username from the token
    fun getUsernameFromToken(token: String): String {
        return Jwts.parser()
            .setSigningKey(secretKey.toByteArray())
            .parseClaimsJws(token)
            .body
            .subject
    }

    // Check if the token has expired
    private fun isTokenExpired(token: String): Boolean {
        val expiration = extractExpiration(token)
        return expiration.before(Date())
    }

    // Extract expiration date from the token
    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    // Extract a specific claim from the token
    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    // Extract all claims from the token
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(secretKey.toByteArray())
            .parseClaimsJws(token)
            .body
    }
}
