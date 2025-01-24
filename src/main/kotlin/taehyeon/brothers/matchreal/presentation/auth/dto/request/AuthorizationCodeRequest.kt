package taehyeon.brothers.matchreal.presentation.auth.dto.request

data class AuthorizationCodeRequest(
    val code: String,
    val redirectUri: String
) 