package taehyeon.brothers.matchreal.domain.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.UnauthorizedException
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
    fun createAccessToken(user: User): String {
        return createToken(user, accessTokenSecretKey, accessTokenValidityInMilliseconds)
    }

    fun createRefreshToken(user: User): String {
        return createToken(user, refreshTokenSecretKey, refreshTokenValidityInMilliseconds)
    }

    fun validateAccessToken(token: String) {
        validateToken(token, accessTokenSecretKey)
    }

    fun validateRefreshToken(token: String) {
        validateToken(token, refreshTokenSecretKey)
    }

    fun getAccessTokenPayload(token: String): String {
        return try {
            val claims = Jwts.parser()
                .setSigningKey(accessTokenSecretKey)
                .parseClaimsJws(token)
                .body
            claims.subject
        } catch (e: Exception) {
            throw UnauthorizedException("Invalid access token", e)
        }
    }

    private fun createToken(user: User, secretKey: String, validityInMilliseconds: Long): String {
        val claims: Claims = Jwts.claims().setSubject(user.id.toString())
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    private fun validateToken(token: String, secretKey: String) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
        } catch (e: JwtException) {
            throw UnauthorizedException("Invalid token", e)
        } catch (e: IllegalArgumentException) {
            throw UnauthorizedException("Invalid token", e)
        }
    }
} 