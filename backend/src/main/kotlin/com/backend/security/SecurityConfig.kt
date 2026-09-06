package com.backend.security

import com.backend.filter.JwtFilter
import com.backend.properties.FrontendProperties
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val frontendProperties: FrontendProperties,
    private val jwtFilter: JwtFilter,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }.csrf { csrf ->
                csrf.disable()
            }.sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.authorizeHttpRequests { auth ->
                auth
                    // Public authentication endpoints
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/user",
                        "/api/auth/login",
                    ).permitAll()
                    // Swagger / OpenAPI
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                    ).permitAll()
                    // Existing public endpoints
                    .requestMatchers(
                        "/actuator/**",
                        "/openapi.yml",
                        "/internal/**",
                    ).permitAll()
                    // Everything else requires authentication
                    .anyRequest()
                    .authenticated()
            }.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter::class.java,
            ).exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint { request, response, authException ->

                        val authentication =
                            SecurityContextHolder
                                .getContext()
                                .authentication

                        // TEMPORARY DEBUG LOGGING
                        println("========================================")
                        println("AUTHENTICATION ENTRY POINT")
                        println("Method: ${request.method}")
                        println("URI: ${request.requestURI}")
                        println("Query: ${request.queryString}")
                        println("Remote address: ${request.remoteAddr}")
                        println(
                            "Authorization header present: " +
                                (request.getHeader("Authorization") != null),
                        )
                        println(
                            "Authentication class: " +
                                (authentication?.javaClass?.name ?: "null"),
                        )
                        println(
                            "Authentication authenticated: " +
                                (authentication?.isAuthenticated ?: false),
                        )
                        println(
                            "Principal: " +
                                (authentication?.principal ?: "null"),
                        )
                        println(
                            "Exception class: " +
                                authException.javaClass.name,
                        )
                        println(
                            "Exception message: " +
                                (authException.message ?: "null"),
                        )
                        println("========================================")

                        response.status = HttpServletResponse.SC_UNAUTHORIZED
                        response.contentType = MediaType.APPLICATION_JSON_VALUE
                        response.characterEncoding = "UTF-8"

                        val exceptionMessage =
                            authException.message
                                ?.replace("\"", "'")
                                ?.replace("\n", " ")
                                ?.replace("\r", " ")
                                ?: "unknown"

                        response.writer.write(
                            """
                            {
                              "status": 401,
                              "message": "Неавторизован или токен истёк",
                              "debug": {
                                "method": "${request.method}",
                                "uri": "${request.requestURI}",
                                "authentication": "${authentication?.javaClass?.simpleName ?: "null"}",
                                "authenticated": ${authentication?.isAuthenticated ?: false},
                                "exception": "${authException.javaClass.simpleName}",
                                "exceptionMessage": "$exceptionMessage"
                              }
                            }
                            """.trimIndent(),
                        )
                    }.accessDeniedHandler { request, response, accessDeniedException ->

                        // TEMPORARY DEBUG LOGGING
                        println("========================================")
                        println("ACCESS DENIED")
                        println("Method: ${request.method}")
                        println("URI: ${request.requestURI}")
                        println(
                            "Authentication: " +
                                SecurityContextHolder
                                    .getContext()
                                    .authentication,
                        )
                        println(
                            "Exception: " +
                                accessDeniedException.javaClass.name,
                        )
                        println(
                            "Message: " +
                                (accessDeniedException.message ?: "null"),
                        )
                        println("========================================")

                        response.status = HttpServletResponse.SC_FORBIDDEN
                        response.contentType = MediaType.APPLICATION_JSON_VALUE
                        response.characterEncoding = "UTF-8"

                        response.writer.write(
                            """
                            {
                              "status": 403,
                              "message": "Недостаточно прав",
                              "debug": {
                                "method": "${request.method}",
                                "uri": "${request.requestURI}",
                                "exception": "${accessDeniedException.javaClass.simpleName}"
                              }
                            }
                            """.trimIndent(),
                        )
                    }
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config =
            CorsConfiguration().apply {
                allowedOrigins =
                    listOf(
                        frontendProperties.url,
                        "http://localhost:5173",
                    )

                allowedMethods =
                    listOf(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS",
                        "PATCH",
                    )

                allowedHeaders = listOf("*")
            }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", config)
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
