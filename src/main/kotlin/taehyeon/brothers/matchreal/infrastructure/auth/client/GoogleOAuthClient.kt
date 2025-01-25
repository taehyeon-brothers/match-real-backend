package taehyeon.brothers.matchreal.infrastructure.auth.client

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.OAuthException
import taehyeon.brothers.matchreal.infrastructure.auth.client.dto.GoogleTokenResponse
import taehyeon.brothers.matchreal.infrastructure.auth.client.dto.GoogleUserResponse
import taehyeon.brothers.matchreal.infrastructure.common.HttpClient
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Component
class GoogleOAuthClient(
    okHttpClient: OkHttpClient,
    objectMapper: ObjectMapper,
    @Value("\${oauth.google.client-id}") private val clientId: String,
    @Value("\${oauth.google.client-secret}") private val clientSecret: String,
    @Value("\${oauth.google.auth-server-url}") private val authServerUrl: String,
    @Value("\${oauth.google.api-server-url}") private val apiServerUrl: String
) : HttpClient(okHttpClient, objectMapper) {

    fun generateAccessToken(authorizationCode: String, redirectUri: String): String {
        val decodedCode: String = URLDecoder.decode(authorizationCode, StandardCharsets.UTF_8)

        val formBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", decodedCode)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("redirect_uri", redirectUri)
            .build()

        val request = buildPostRequest("$authServerUrl/token", formBody)
        val response = executeRequest<GoogleTokenResponse>(request)
        return response.accessToken ?: throw OAuthException("Access token is null")
    }

    fun getUserInfo(accessToken: String): User {
        val request = buildGetRequest(
            url = "$apiServerUrl/oauth2/v2/userinfo",
            headers = mapOf("Authorization" to "Bearer $accessToken")
        )
        return executeRequest<GoogleUserResponse>(request).toUser()
    }
} 