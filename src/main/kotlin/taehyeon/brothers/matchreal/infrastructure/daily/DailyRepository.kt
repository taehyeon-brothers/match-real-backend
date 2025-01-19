package taehyeon.brothers.matchreal.infrastructure.daily

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import taehyeon.brothers.matchreal.domain.Daily

@Repository
interface DailyRepository : JpaRepository<Daily, Long> {
}
