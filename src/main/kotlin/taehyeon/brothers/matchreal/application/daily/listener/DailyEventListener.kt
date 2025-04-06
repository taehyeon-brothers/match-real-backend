package taehyeon.brothers.matchreal.application.daily.listener

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import taehyeon.brothers.matchreal.application.tag.service.TagService
import taehyeon.brothers.matchreal.domain.daily.event.DailyUploadedEvent
import taehyeon.brothers.matchreal.exception.database.EntityNotFoundException
import taehyeon.brothers.matchreal.infrastructure.daily.repository.DailyRepository

@Component
class DailyEventListener(
    private val tagService: TagService,
    private val dailyRepository: DailyRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleDailyUploadedEvent(event: DailyUploadedEvent) {
        try {
            val daily = dailyRepository.findById(event.dailyId)
                .orElseThrow { EntityNotFoundException(message = "데일리가 존재하지 않습니다. dailyId: ${event.dailyId}") }
            
            tagService.addTagsByDailyImage(
                daily,
                daily.imageContent,
                daily.imageContentType
            )
        } catch (e: Exception) {
            log.error("태그 추출 중 오류 발생: ${e.message}", e)
        }
    }
}
