package taehyeon.brothers.matchreal.application.daily.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.daily.event.DailyUploadedEvent
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.business.NotFoundImageException
import taehyeon.brothers.matchreal.exception.database.EntityNotFoundException
import taehyeon.brothers.matchreal.infrastructure.daily.repository.DailyRepository
import taehyeon.brothers.matchreal.infrastructure.tag.repository.TagRepository
import taehyeon.brothers.matchreal.presentation.daily.dto.response.DailyDetailResponse
import taehyeon.brothers.matchreal.presentation.daily.dto.response.FeedDailyResponses
import taehyeon.brothers.matchreal.presentation.daily.dto.response.FeedRawDailyResponse

@Service
@Transactional
class DailyService(
    private val dailyRepository: DailyRepository,
    private val tagRepository: TagRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    fun uploadDaily(user: User, file: MultipartFile): Daily {
        val filename = file.originalFilename ?: throw NotFoundImageException()
        val fileContentType = file.contentType ?: MediaType.IMAGE_JPEG.type
        val fileBinaryContent = file.bytes

        val daily = Daily.createForm(user, filename, fileContentType, fileBinaryContent)
        val savedDaily = dailyRepository.save(daily)

        applicationEventPublisher.publishEvent(DailyUploadedEvent(savedDaily.id))

        return savedDaily
    }

    @Transactional(readOnly = true)
    fun findDailyById(dailyId: Long): DailyDetailResponse {
        val tags = tagRepository.findByDailyId(dailyId)
        val daily = dailyRepository.findById(dailyId)
            .orElseThrow { EntityNotFoundException(message = "데일리가 존재하지 않습니다. dailyId: $dailyId") }
        return DailyDetailResponse.of(daily, daily.user, tags)
    }

    @Transactional(readOnly = true)
    fun findAllDailies(currentPage: Int, contentSize: Int): FeedDailyResponses {
        // TODO("현재는 최신순 정렬, 이후 GPT API 연동 및 태그 유사도 순 반영할 것")

        val requestPage = PageRequest.of(currentPage, contentSize, Sort.by("createdAt").descending())
        val dailiesWithPageInfo = dailyRepository.findAllBy(requestPage)
            .map { FeedRawDailyResponse.from(it) }
        return FeedDailyResponses.of(
            dailiesWithPageInfo.number + 1, // 프론트는 1페이지부터, Spring data jpa 페이지네이션은 0페이지부터 시작.
            dailiesWithPageInfo.totalPages,
            dailiesWithPageInfo.content
        )
    }
}
