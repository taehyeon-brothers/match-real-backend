package taehyeon.brothers.matchreal.infrastructure.auth.client

import taehyeon.brothers.matchreal.domain.user.User

interface OAuthClient {
    fun generateAccessToken(authorizationCode: String, redirectUri: String): String
    fun getUserInfo(accessToken: String): User
} 