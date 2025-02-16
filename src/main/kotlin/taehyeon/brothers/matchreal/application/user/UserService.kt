package taehyeon.brothers.matchreal.application.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.database.EntityNotFoundException
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.presentation.user.dto.request.UpdateUserRequest

@Service
class UserService(
    private val userRepository: UserRepository
) {
    
    @Transactional(readOnly = true)
    fun getUser(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { EntityNotFoundException(message = "존재하지 않는 사용자입니다.") }
    }

    @Transactional
    fun updateUser(id: Long, request: UpdateUserRequest): User {
        val user = getUser(id)
        user.updateProfile(
            nickname = request.nickname,
            gender = request.gender,
            age = request.age,
            introduction = request.introduction,
            openChatUrl = request.openChatUrl
        )
        return userRepository.save(user)
    }
}
