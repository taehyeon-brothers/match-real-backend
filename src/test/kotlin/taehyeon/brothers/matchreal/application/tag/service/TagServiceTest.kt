package taehyeon.brothers.matchreal.application.tag.service

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import taehyeon.brothers.matchreal.domain.auth.JwtTokenProvider
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.infrastructure.daily.repository.DailyRepository
import taehyeon.brothers.matchreal.infrastructure.tag.repository.TagRepository
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.support.fixture.DailyFixture
import taehyeon.brothers.matchreal.support.fixture.UserFixture

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TagServiceTest {

    @Autowired
    private lateinit var tagService: TagService

    @Autowired
    private lateinit var dailyRepository: DailyRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var testUser: User
    private lateinit var testDaily: Daily
    private lateinit var testRefreshToken: String

    @BeforeEach
    fun setUp() {
        tagRepository.deleteAll()
        dailyRepository.deleteAll()
        userRepository.deleteAll()

        val user = userRepository.save(UserFixture.create())
        testRefreshToken = jwtTokenProvider.createRefreshToken(user)
        user.updateRefreshToken(testRefreshToken)
        testUser = userRepository.save(user)
        testDaily = dailyRepository.save(DailyFixture.create(user = testUser))
    }

    @Test
    @DisplayName("데일리 업로드 시 태그 분류")
    fun addTagsByDailyImage() {
        // given
        val dailyImage = DailyFixture.createDailyImage()

        // when
        tagService.addTagsByDailyImage(testDaily, dailyImage)

        // then
        val tags = tagRepository.findByDailyId(testDaily.id)
        tags.map { it.tagName } shouldContainExactly listOf("연애", "기쁨", "성수동", "영화", "데이트")
    }

    @Test
    @DisplayName("데일리 내 태그 추가")
    fun addTagByUser() {
        // given
        val tagName = "제이온"

        // when
        val tagId = tagService.addTagByUser(testDaily.id, tagName)

        // then
        val result = tagRepository.findById(tagId).orElseThrow()
        result.id shouldBeEqual tagId
        result.daily shouldBeEqual testDaily
        result.tagName shouldBeEqual tagName
    }

    @Test
    @DisplayName("데일리 내 태그 제거")
    fun removeTagByUser() {
        // given
        val tagId = tagRepository.save(DailyFixture.createTag(daily = testDaily)).id

        // when
        tagService.removeTagByUser(tagId = tagId)

        // then
        val tags = tagRepository.findByDailyId(testDaily.id)
        tags shouldHaveSize 0
    }
}
