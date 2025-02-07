package taehyeon.brothers.matchreal.application.tag.service

import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener
import org.springframework.web.multipart.MultipartFile
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.tag.Tag
import taehyeon.brothers.matchreal.infrastructure.tag.repository.TagRepository

@Service
class TagService(
    private val tagRepository: TagRepository,
) {

    @TransactionalEventListener
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
}
