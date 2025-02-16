package taehyeon.brothers.matchreal.application.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.database.EntityNotFoundException
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository
) {
    
    @Transactional(readOnly = true)
    fun getUser(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { EntityNotFoundException(message = "존재하지 않는 사용자입니다.") }
    }
}
