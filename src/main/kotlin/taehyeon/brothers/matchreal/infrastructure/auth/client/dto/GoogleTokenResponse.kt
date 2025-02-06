package taehyeon.brothers.matchreal.infrastructure.auth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GoogleTokenResponse(
    val accessToken: String? = null,
    val expiresIn: Int? = null,
    val scope: String? = null,
    val tokenType: String? = null,
    val idToken: String? = null
)
