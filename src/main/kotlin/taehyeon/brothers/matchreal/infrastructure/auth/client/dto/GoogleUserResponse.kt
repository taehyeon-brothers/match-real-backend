package taehyeon.brothers.matchreal.infrastructure.auth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import taehyeon.brothers.matchreal.domain.auth.OAuthProvider
import taehyeon.brothers.matchreal.domain.user.User

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GoogleUserResponse(
    val id: String? = null,
    val email: String? = null,
    val verifiedEmail: Boolean? = null,
    val name: String? = null,
    val givenName: String? = null,
    val familyName: String? = null,
    val picture: String? = null,
    val locale: String? = null
) {
    fun toUser(): User = User.createFrom(
        nickname = name ?: throw IllegalStateException("Name cannot be null"),
        email = email ?: throw IllegalStateException("Email cannot be null"),
        oauthId = id ?: throw IllegalStateException("ID cannot be null"),
        oauthProvider = OAuthProvider.GOOGLE,
        profileImageUrl = picture
    )
} 