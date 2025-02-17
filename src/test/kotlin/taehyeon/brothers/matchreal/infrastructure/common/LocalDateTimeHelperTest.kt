package taehyeon.brothers.matchreal.infrastructure.common

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import java.time.LocalDateTime
import java.time.Month
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class LocalDateTimeHelperTest {

    @AfterEach
    fun tearDown() {
        LocalDateTimeHelper.unfixCurrentTime()
    }

    @Test
    fun fixCurrentTime() {
        // given
        // when
        LocalDateTimeHelper.fixCurrentTime(LocalDateTime.of(2025, 2, 16, 11, 30, 55))

        // then
        val now = LocalDateTimeHelper.now()
        now.month shouldBeEqual Month.FEBRUARY
        now.dayOfMonth shouldBeEqual 16
        now.hour shouldBeEqual 11
        now.minute shouldBeEqual 30
        now.second shouldBeEqual 55
    }

    @Test
    fun unfixCurrentTime() {
        // given
        val notNow = LocalDateTime.of(2025, 2, 16, 11, 30, 55)
        println("notNow: $notNow")
        LocalDateTimeHelper.fixCurrentTime(notNow)

        // when
        LocalDateTimeHelper.unfixCurrentTime()

        // then
        val now = LocalDateTimeHelper.now()
        println("now: $now")
        now shouldNotBeEqual notNow
    }
}
