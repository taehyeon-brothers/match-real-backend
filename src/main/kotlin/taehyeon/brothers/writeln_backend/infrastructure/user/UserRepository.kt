package taehyeon.brothers.writeln_backend.infrastructure.user

import org.springframework.data.jpa.repository.JpaRepository
import taehyeon.brothers.writeln_backend.domain.User

interface UserRepository : JpaRepository<User, Long>
