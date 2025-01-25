package taehyeon.brothers.matchreal.domain.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.network.UnauthorizedException
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${security.jwt.access-token.secret-key}")
    private val accessTokenSecretKey: String,
    
    @Value("\${security.jwt.refresh-token.secret-key}")
    private val refreshTokenSecretKey: String,
    
    @Value("\${security.jwt.access-token.expire-length}")
    private val accessTokenValidityInMilliseconds: Long,
    
    @Value("\${security.jwt.refresh-token.expire-length}")
    private val refreshTokenValidityInMilliseconds: Long
) {
    private val accessSigningKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(accessTokenSecretKey.toByteArray())
    }
    
    private val refreshSigningKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(refreshTokenSecretKey.toByteArray())
    }

    fun createAccessToken(user: User): String {
        return createToken(user, accessSigningKey, accessTokenValidityInMilliseconds)
    }

    fun createRefreshToken(user: User): String {
        return createToken(user, refreshSigningKey, refreshTokenValidityInMilliseconds)
    }

    fun validateAccessToken(token: String) {
        validateToken(token, accessSigningKey)
    }

    fun validateRefreshToken(token: String) {
        validateToken(token, refreshSigningKey)
    }

    fun getAccessTokenPayload(token: String): String {
        return try {
            Jwts.parser()
                .verifyWith(accessSigningKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (e: Exception) {
            throw UnauthorizedException(message = "Invalid access token. e: $e")
        }
    }

    private fun createToken(user: User, key: SecretKey, validityInMilliseconds: Long): String {
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .subject(user.id.toString())
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }

    private fun validateToken(token: String, key: SecretKey) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
        } catch (e: JwtException) {
            throw UnauthorizedException(message = "Invalid token. e: $e")
        } catch (e: IllegalArgumentException) {
            throw UnauthorizedException(message = "Invalid token. e: $e")
        }
    }
} 