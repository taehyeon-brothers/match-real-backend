package taehyeon.brothers.matchreal.presentation.user.dto.request

import taehyeon.brothers.matchreal.domain.user.Gender

data class UpdateUserRequest(
    val nickname: String? = null,
    val gender: Gender? = null,
    val age: Int? = null,
    val introduction: String? = null,
    val openChatUrl: String? = null
)
