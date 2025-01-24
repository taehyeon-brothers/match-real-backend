package taehyeon.brothers.matchreal.infrastructure.auth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GoogleTokenResponse(
    val accessToken: String,
    val expiresIn: Int,
    val scope: String,
    val tokenType: String,
    val idToken: String?
) 