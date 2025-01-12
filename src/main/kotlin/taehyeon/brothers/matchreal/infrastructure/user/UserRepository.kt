package taehyeon.brothers.matchreal.infrastructure.user

import org.springframework.data.jpa.repository.JpaRepository
import taehyeon.brothers.matchreal.domain.User

interface UserRepository : JpaRepository<User, Long>
