package com.backend.jwt

import com.backend.properties.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
) {
    private fun key(): SecretKey =
        Keys.hmacShaKeyFor(
            Base64.getDecoder().decode(jwtProperties.secret),
        )

    fun generate(
        userId: UUID,
        email: String,
    ): String =
        Jwts
            .builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", "USER")
            .issuedAt(Date())
            .expiration(
                Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L),
            ).signWith(key())
            .compact()

    fun validate(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(key())
            .build()
            .parseSignedClaims(token)
            .payload
}
