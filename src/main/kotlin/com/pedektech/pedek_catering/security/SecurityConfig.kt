package com.pedektech.pedek_catering.security


import com.pedektech.pedek_catering.util.JwtUtil
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtUtil: JwtUtil,
   // private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .authorizeRequests { requests ->
                requests
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/user",
                        "/favicon.ico",
                        "/api/user/login",
                        "/api/v1/campaign/**",
                        "/api/login",
                        "api/aboutMinistry",
                        "api/campRules",
                        "api/products",
                        "/api/campaign/products",
                        "/api/v1/campaign/create",
                        "api/favourites/**",
                        "api/user/reset-password/request",
                        "api/user/reset-password/verify-otp",
                        "/api/user/reset-password/update",
                        "/api/sendOtp",
                        "/api/verifyOtp",
                        "/api/images/upload",
                        "/api/upload",
                        "/error" // Allow access to error page
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            //.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint { request, response, authException ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
            }
            .accessDeniedHandler { request, response, accessDeniedException ->
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")
            }

        return http.build()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}
