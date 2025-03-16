package taehyeon.brothers.matchreal.infrastructure.daily.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import taehyeon.brothers.matchreal.domain.daily.Daily

@Repository
interface DailyRepository : JpaRepository<Daily, Long> {

    fun findAllBy(request: Pageable): Page<Daily>
}
