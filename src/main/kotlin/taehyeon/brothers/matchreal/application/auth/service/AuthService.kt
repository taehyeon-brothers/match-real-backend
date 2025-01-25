package taehyeon.brothers.matchreal.application.auth.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import taehyeon.brothers.matchreal.domain.auth.JwtTokenProvider
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.database.EntityNotFoundException
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.infrastructure.auth.client.GoogleOAuthClient
import taehyeon.brothers.matchreal.presentation.auth.dto.request.AuthorizationCodeRequest
import taehyeon.brothers.matchreal.presentation.auth.dto.response.TokenResponse

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val googleOAuthClient: GoogleOAuthClient
) {
    @Transactional
    fun googleLogin(request: AuthorizationCodeRequest): TokenResponse {
        val accessToken = googleOAuthClient.generateAccessToken(request.code, request.redirectUri)
        val user = findOrCreateUser(googleOAuthClient.getUserInfo(accessToken))

        val jwtAccessToken = jwtTokenProvider.createAccessToken(user)
        val refreshToken = jwtTokenProvider.createRefreshToken(user)
        
        user.updateRefreshToken(refreshToken)
        userRepository.save(user)

        return TokenResponse(jwtAccessToken, refreshToken)
    }

    @Transactional
    fun refreshAccessToken(refreshToken: String): TokenResponse {
        jwtTokenProvider.validateRefreshToken(refreshToken)

        val user = userRepository.findByRefreshToken(refreshToken)
            ?: throw EntityNotFoundException(message = "Not found User. refreshToken: $refreshToken")

        val newAccessToken = jwtTokenProvider.createAccessToken(user)
        return TokenResponse(newAccessToken, refreshToken)
    }

    @Transactional
    fun logout(user: User) {
        user.updateRefreshToken(null)
        userRepository.save(user)
    }

    private fun findOrCreateUser(user: User): User {
        return userRepository.findByOauthId(user.oauthId) ?: userRepository.save(user)
    }

    fun findUserByAccessToken(accessToken: String?): User? {
        if (accessToken == null) {
            return null
        }
        jwtTokenProvider.validateAccessToken(accessToken)
        val userId = jwtTokenProvider.getAccessTokenPayload(accessToken)
        return userRepository.findById(userId.toLong()).orElse(null)
    }
} 