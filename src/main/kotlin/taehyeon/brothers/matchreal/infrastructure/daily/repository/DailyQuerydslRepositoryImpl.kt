package taehyeon.brothers.matchreal.infrastructure.daily.repository

import com.querydsl.core.types.dsl.BooleanExpression
import java.time.LocalDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.daily.QDaily.daily
import taehyeon.brothers.matchreal.infrastructure.config.QuerydslRepositorySupport

@Repository
class DailyQuerydslRepositoryImpl :
    QuerydslRepositorySupport(Daily::class.java),
    DailyQuerydslRepository {
    override fun findAllBy(
        userId: Long?,
        targetDateFrom: LocalDateTime?,
        targetDateTo: LocalDateTime?,
        pageable: Pageable
    ): Page<Daily> {
        val fetchResults = from(daily)
            .where(
                withValue(userId) { daily.user.id.eq(it) },
                withValue(targetDateFrom) { daily.createdAt.goe(it) },
                withValue(targetDateTo) { daily.createdAt.loe(it) }
            )
            .orderBy(daily.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()

        return PageImpl(fetchResults.results, pageable, fetchResults.total)
    }
}

inline fun <T> withValue(
    value: T?,
    predicate: (T) -> BooleanExpression,
): BooleanExpression? =
    when (value) {
        is Collection<*> -> if (value.isEmpty()) null else predicate(value)
        else -> value?.let(predicate)
    }
