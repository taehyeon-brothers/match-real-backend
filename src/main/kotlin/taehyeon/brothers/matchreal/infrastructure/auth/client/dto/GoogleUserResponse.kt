package taehyeon.brothers.matchreal.infrastructure.auth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import taehyeon.brothers.matchreal.domain.auth.OAuthProvider
import taehyeon.brothers.matchreal.domain.user.User

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GoogleUserResponse(
    val id: String,
    val email: String,
    val verifiedEmail: Boolean,
    val name: String,
    val givenName: String?,
    val familyName: String?,
    val picture: String?,
    val locale: String?
) {
    fun toUser(): User = User.createFrom(
        nickname = name,
        email = email,
        oauthId = id,
        oauthProvider = OAuthProvider.GOOGLE,
        profileImageUrl = picture
    )
} 