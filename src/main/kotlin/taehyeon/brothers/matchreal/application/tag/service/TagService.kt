package taehyeon.brothers.matchreal.application.tag.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.tag.Tag
import taehyeon.brothers.matchreal.exception.database.EntityNotFoundException
import taehyeon.brothers.matchreal.infrastructure.daily.repository.DailyRepository
import taehyeon.brothers.matchreal.infrastructure.tag.repository.TagRepository

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val dailyRepository: DailyRepository,
) {

    fun addTagsByDailyImage(daily: Daily, dailyImage: MultipartFile): List<Tag> {
        /**
         * TODO: 딥러닝을 이용하여 이미지 파일로 태그 분류
         * 현재는 임시로 mock tag 반환
         */
        val tags = listOf(
            Tag.createForm(daily, "연애"),
            Tag.createForm(daily, "기쁨"),
            Tag.createForm(daily, "성수동"),
            Tag.createForm(daily, "영화"),
            Tag.createForm(daily, "데이트"),
        )
        return tagRepository.saveAll(tags)
    }

    @Transactional
    fun addTagByUser(dailyId: Long, tagName: String): Long {
        val daily = dailyRepository.findById(dailyId)
            .orElseThrow { EntityNotFoundException(message = "데일리가 존재하지 않습니다. dailyId: $dailyId") }
        val tag = Tag.createForm(daily, tagName)
        val savedTag = tagRepository.save(tag)
        return savedTag.id
    }

    @Transactional
    fun removeTagByUser(tagId: Long) {
        tagRepository.findById(tagId)
            .orElseThrow { EntityNotFoundException(message = "태그가 존재하지 않습니다. tagId: $tagId") }
        tagRepository.deleteById(tagId)
    }
}
