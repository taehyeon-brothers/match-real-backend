package taehyeon.brothers.matchreal.infrastructure.daily.repository

import java.time.LocalDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import taehyeon.brothers.matchreal.domain.daily.Daily

@Repository
interface DailyQuerydslRepository {

    fun findAllBy(
        userId: Long?,
        targetDateFrom: LocalDateTime?,
        targetDateTo: LocalDateTime?,
        pageable: Pageable
    ): Page<Daily>
}
