package taehyeon.brothers.matchreal.application.daily.service

import java.time.LocalTime
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.business.DailyUploadTimeException
import taehyeon.brothers.matchreal.exception.business.NotFoundImageException
import taehyeon.brothers.matchreal.infrastructure.common.LocalDateTimeHelper
import taehyeon.brothers.matchreal.infrastructure.daily.repository.DailyRepository

@Service
@Transactional
class DailyService(
    private val dailyRepository: DailyRepository,
) {

    fun uploadDaily(user: User, file: MultipartFile): Daily {
        validateUploadTime()
        val filename = file.originalFilename ?: throw NotFoundImageException()
        val fileContentType = file.contentType ?: MediaType.IMAGE_JPEG.type
        val fileBinaryContent = file.bytes

        val daily = Daily.createForm(user, filename, fileContentType, fileBinaryContent)
        return dailyRepository.save(daily)
    }

    private fun validateUploadTime() {
        val now = LocalDateTimeHelper.now().toLocalTime()
        val start = LocalTime.of(13, 0)
        val end = LocalTime.of(19, 0)

        if (now.isBefore(start) || now.isAfter(end)) {
            throw DailyUploadTimeException()
        }
    }
}
