package taehyeon.brothers.writeln_backend.infrastructure.daily

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import taehyeon.brothers.writeln_backend.domain.Daily

@Repository
interface DailyRepository : JpaRepository<Daily, Long> {
}
