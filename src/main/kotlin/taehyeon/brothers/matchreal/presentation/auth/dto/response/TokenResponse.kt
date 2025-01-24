package taehyeon.brothers.matchreal.presentation.auth.dto.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
) 