package com.backend.filter

import com.backend.jwt.JwtProvider
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
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val header = request.getHeader("Authorization")

        if (header != null && header.startsWith("Bearer ")) {
            try {
                val token = header.substring(7)
                val claims: Claims = jwtProvider.validate(token)

                val userId = UUID.fromString(claims.subject)
                val role = claims.get("role", String::class.java)

                val authorities =
                    if (role != null) {
                        listOf(
                            SimpleGrantedAuthority("ROLE_$role"),
                        )
                    } else {
                        emptyList()
                    }

                val auth =
                    UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        authorities,
                    )

                auth.details = claims

                SecurityContextHolder
                    .getContext()
                    .authentication = auth
            } catch (e: JwtException) {
                SecurityContextHolder.clearContext()
            } catch (e: IllegalArgumentException) {
                // Invalid UUID in JWT subject
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }
}
