package com.edumora.trayectoria.infrastructure.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

/**
 * @Service -> Spring registers this class as a managed bean.
 * @Value   -> Spring injects the value from application.yml / .env
 * at the time the bean is constructed.
 *
 * Single Responsibility: focused solely on JWT operations.
 * It is decoupled from HTTP, the database, and Spring Security filters.
 */
@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.expiration-ms}")
    private val expirationMs: Long,

    @Value("\${jwt.refresh-expiration-ms}")
    private val refreshExpirationMs: Long
) {

    //  Generación de tokens 

    /**
     * Generates a JWT access token for the authenticated user.
     * The subject is the user's email.
     * Authorities (roles) are included as an additional claim.
     */
    fun generateAccessToken(userDetails: UserDetails): String {
        val authorities = userDetails.authorities
            .joinToString(",") { it.authority }

        return Jwts.builder()
            .subject(userDetails.username)              // username = email en la app
            .claim("authorities", authorities)           // roles en el token
            .issuedAt(Date())                           // fecha de emisión
            .expiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(signingKey())                      // firma con HMAC-SHA256
            .compact()                                      // genera el token en formato String
    }

    /**
     * Generates a refresh token — same subject, no authorities,
     * with a longer duration. Used to renew the access token
     * without requesting credentials again.
     */
    fun generateRefreshToken(userDetails: UserDetails): String {
        return Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + refreshExpirationMs))
            .signWith(signingKey())
            .compact()
    }

    //  Extracción de datos

    fun extractEmail(token: String): String =
        extractAllClaims(token).subject

    fun extractExpiration(token: String): Date =
        extractAllClaims(token).expiration

    // Validación

    /**
     * Validates that the token:
     * 1. Has a valid signature (has not been tampered with).
     * 2. Is not expired.
     * 3. Belongs to the user presenting it.
     */
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        return try {
            val email = extractEmail(token)
            email == userDetails.username && !isTokenExpired(token)
        } catch (e: JwtException) {
            // Token malformado, firma inválida, etc.
            false
        }
    }

    fun isTokenExpired(token: String): Boolean =
        extractExpiration(token).before(Date())

    //  Privados 

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)           // Verifica la firma y parsea el token
            .payload                                 // Extrae el payload (claims) del token, es decir, el contenido con email, roles, etc.
    }

    /**
     * Converts the Base64 secret into a SecretKey for HMAC-SHA256.
     * The key is retrieved from the .env file.
     */
    private fun signingKey(): SecretKey =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
}
