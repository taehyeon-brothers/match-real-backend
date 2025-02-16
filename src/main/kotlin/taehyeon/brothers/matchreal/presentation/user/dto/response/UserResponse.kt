package taehyeon.brothers.matchreal.presentation.user.dto.response

import taehyeon.brothers.matchreal.domain.user.Gender
import taehyeon.brothers.matchreal.domain.user.User
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val nickname: String,
    val email: String,
    val age: Int?,
    val gender: Gender?,
    val introduction: String?,
    val profileImageUrl: String?,
    val openChatUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(user: User) = UserResponse(
            id = user.id,
            nickname = user.nickname,
            email = user.email,
            age = user.age,
            gender = user.gender,
            introduction = user.introduction,
            profileImageUrl = user.profileImageUrl,
            openChatUrl = user.openChatUrl,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}
