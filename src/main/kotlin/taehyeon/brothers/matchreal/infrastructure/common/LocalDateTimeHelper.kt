package taehyeon.brothers.matchreal.infrastructure.common

import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class LocalDateTimeHelper {

    companion object {

        private var clock = Clock.system(zoneId())

        /**
         * 해당 now 를 사용하면, 우리가 시스템 시각을 커스터마이징 가능하다.
         */
        fun now(): LocalDateTime {
            return LocalDateTime.now(clock)
        }

        /**
         * 시스템 시각을 특정 시각으로 변경하여 고정한다.
         * 해당 함수를 사용한 뒤에는 반드시 unfixCurrentTime 를 사용하여 원래의 system time 으로 변경해야 한다.
         * @see unfixCurrentTime
         */
        fun fixCurrentTime(localDateTime: LocalDateTime) {
            clock = Clock.fixed(ZonedDateTime.of(localDateTime, zoneId()).toInstant(), zoneId())
        }

        /**
         * 시스템 시각을 한국 기본값 (UTC+9) 로 세팅 원복한다.
         */
        fun unfixCurrentTime() {
            clock = Clock.system(zoneId())
        }

        private fun zoneId(): ZoneId = ZoneId.of("Asia/Seoul")
    }
}
