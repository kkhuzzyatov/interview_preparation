package com.backend.filter

import com.backend.jwt.JwtProvider
import com.backend.user.repository.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.UUID

@Component
class JwtFilter(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader?.startsWith(BEARER_PREFIX) == true) {
            authenticate(authorizationHeader.removePrefix(BEARER_PREFIX))
        }

        filterChain.doFilter(request, response)
    }

    private fun authenticate(token: String) {
        try {
            val claims: Claims = jwtProvider.validate(token)
            val userId = UUID.fromString(claims.subject)

            val user =
                userRepository
                    .findById(userId)
                    .orElseThrow {
                        JwtException("User not found")
                    }

            val authority =
                SimpleGrantedAuthority("ROLE_${user.role.name}")

            val authentication =
                UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    listOf(authority),
                ).apply {
                    details = claims
                }

            SecurityContextHolder.getContext().authentication = authentication
        } catch (e: JwtException) {
            SecurityContextHolder.clearContext()
        } catch (e: IllegalArgumentException) {
            // Invalid UUID in JWT subject.
            SecurityContextHolder.clearContext()
        }
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
    }
}
