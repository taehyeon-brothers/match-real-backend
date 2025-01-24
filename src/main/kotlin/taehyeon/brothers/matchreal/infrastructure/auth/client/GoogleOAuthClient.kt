package taehyeon.brothers.matchreal.infrastructure.auth.client

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.infrastructure.common.HttpClient
import taehyeon.brothers.matchreal.infrastructure.auth.client.dto.GoogleTokenResponse
import taehyeon.brothers.matchreal.infrastructure.auth.client.dto.GoogleUserResponse

@Component
class GoogleOAuthClient(
    okHttpClient: OkHttpClient,
    objectMapper: ObjectMapper,
    @Value("\${oauth.google.client-id}") private val clientId: String,
    @Value("\${oauth.google.client-secret}") private val clientSecret: String,
    @Value("\${oauth.google.auth-server-url}") private val authServerUrl: String,
    @Value("\${oauth.google.api-server-url}") private val apiServerUrl: String
) : HttpClient(okHttpClient, objectMapper), OAuthClient {

    override fun generateAccessToken(authorizationCode: String, redirectUri: String): String {
        val formBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", authorizationCode)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("redirect_uri", redirectUri)
            .build()

        val request = buildPostRequest("$authServerUrl/token", formBody)
        return executeRequest<GoogleTokenResponse>(request).accessToken
    }

    override fun getUserInfo(accessToken: String): User {
        val request = buildGetRequest(
            url = "$apiServerUrl/oauth2/v2/userinfo",
            headers = mapOf("Authorization" to "Bearer $accessToken")
        )
        return executeRequest<GoogleUserResponse>(request).toUser()
    }
} 