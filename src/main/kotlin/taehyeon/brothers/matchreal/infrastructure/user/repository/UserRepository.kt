package taehyeon.brothers.matchreal.infrastructure.user.repository

import taehyeon.brothers.matchreal.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByOauthId(oauthId: String): User?
    fun findByEmail(email: String): User?
    fun findByRefreshToken(refreshToken: String): User?
} 