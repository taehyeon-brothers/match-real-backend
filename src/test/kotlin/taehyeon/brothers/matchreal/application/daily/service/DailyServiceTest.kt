package taehyeon.brothers.matchreal.application.daily.service

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import taehyeon.brothers.matchreal.domain.auth.JwtTokenProvider
import taehyeon.brothers.matchreal.domain.daily.event.DailyUploadedEvent
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.infrastructure.common.LocalDateTimeHelper
import taehyeon.brothers.matchreal.infrastructure.daily.repository.DailyRepository
import taehyeon.brothers.matchreal.infrastructure.tag.repository.TagRepository
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.support.fixture.DailyFixture
import taehyeon.brothers.matchreal.support.fixture.TagFixture
import taehyeon.brothers.matchreal.support.fixture.UserFixture

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DailyServiceTest {

    @Autowired
    private lateinit var dailyRepository: DailyRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var dailyService: DailyService

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var testUser1: User
    private lateinit var testUser2: User
    private lateinit var testRefreshToken1: String
    private lateinit var testRefreshToken2: String
    private val today: LocalDateTime = LocalDateTime.of(2025, 2, 16, 13, 30, 0)

    @BeforeEach
    fun setUp() {
        LocalDateTimeHelper.fixCurrentTime(today)
        val user1 = userRepository.save(UserFixture.create())
        testRefreshToken1 = jwtTokenProvider.createRefreshToken(user1)
        user1.updateRefreshToken(testRefreshToken1)
        testUser1 = userRepository.save(user1)

        val user2 = userRepository.save(UserFixture.create())
        testRefreshToken2 = jwtTokenProvider.createRefreshToken(user2)
        user2.updateRefreshToken(testRefreshToken2)
        testUser2 = userRepository.save(user2)
    }

    @AfterEach
    fun tearDown() {
        dailyRepository.deleteAll()
        userRepository.deleteAll()
        LocalDateTimeHelper.unfixCurrentTime()
    }

    @Test
    @DisplayName("데일리 업로드 - 정상 케이스")
    fun dailyUpload() {
        // given
        val dailyImage = DailyFixture.createDailyImage()

        // when
        val daily = dailyService.uploadDaily(testUser1, dailyImage)

        // then
        assertThat(daily).isNotNull()
        daily.user shouldBe testUser1
        daily.imageName shouldBe "클라이밍"
        daily.imageContentType shouldBe MediaType.IMAGE_JPEG.type
        daily.imageContent shouldBe "testImg".toByteArray()
    }

    @Test
    @DisplayName("데일리 업로드 시 이벤트 발행 확인")
    fun dailyUploadPublishEvent() {
        // given
        val mockEventPublisher = mock(ApplicationEventPublisher::class.java)
        val testDailyService = DailyService(dailyRepository, tagRepository, mockEventPublisher)
        val dailyImage = DailyFixture.createDailyImage()
        val eventCaptor = ArgumentCaptor.forClass(DailyUploadedEvent::class.java)

        // when
        val daily = testDailyService.uploadDaily(testUser1, dailyImage)

        // then
        verify(mockEventPublisher, times(1)).publishEvent(eventCaptor.capture())
        val capturedEvent = eventCaptor.value
        capturedEvent.dailyId shouldBeEqual daily.id
    }

    @Test
    @DisplayName("데일리 업로드 - 이미 업로드를 한 케이스도 다른 이미지 업로드 시 정상으로 처리")
    fun dailyUploadDuplicate() {
        // given
        val dailyImage = DailyFixture.createDailyImage()

        // when
        val daily = dailyService.uploadDaily(testUser1, dailyImage)

        // then
        assertThat(daily).isNotNull()
    }

    @Test
    @DisplayName("데일리 및 태그 조회")
    fun findDailyById() {
        // given
        val daily = dailyRepository.save(DailyFixture.create(user = testUser1))
        val tag1 = tagRepository.save(TagFixture.create(daily = daily, tagName = "클라이밍"))
        val tag2 = tagRepository.save(TagFixture.create(daily = daily, tagName = "정적취미"))
        tagRepository.save(tag1)
        tagRepository.save(tag2)

        // when
        val result = dailyService.findDailyById(dailyId = daily.id)

        // then
        result.dailyId shouldBe daily.id
        result.userId shouldBe daily.user.id
        result.tags shouldHaveSize 2
        result.tags.get(0).tagName shouldBe "클라이밍"
        result.tags.get(1).tagName shouldBe "정적취미"
    }

    @Test
    @DisplayName("데일리 이미지 조회")
    fun findDailyImageById() {
        // given
        val daily = dailyRepository.save(DailyFixture.create(user = testUser1))

        // when
        val result = dailyService.findDailyImageById(dailyId = daily.id)

        // then
        result.id shouldBeEqual daily.id
        result.imageContentType shouldBe MediaType.IMAGE_JPEG.type
        result.imageName shouldBeEqual "클라이밍"
    }

    @Test
    @DisplayName("피드 데일리 목록 조회 - 최신순 정렬")
    fun findAllDailies() {
        // given
        val daily1 =
            dailyRepository.save(DailyFixture.createWithBaseTime(user = testUser1, createdAt = today.minusDays(1)))
        val daily2 =
            dailyRepository.save(DailyFixture.createWithBaseTime(user = testUser1, createdAt = today.minusDays(1)))

        val daily3 = dailyRepository.save(DailyFixture.createWithBaseTime(user = testUser1, createdAt = today))
        val daily4 = dailyRepository.save(DailyFixture.createWithBaseTime(user = testUser1, createdAt = today))
        val daily5 = dailyRepository.save(DailyFixture.createWithBaseTime(user = testUser2, createdAt = today))

        val daily6 =
            dailyRepository.save(DailyFixture.createWithBaseTime(user = testUser1, createdAt = today.plusDays(1)))
        val daily7 =
            dailyRepository.save(DailyFixture.createWithBaseTime(user = testUser2, createdAt = today.plusDays(1)))


        // when
        val result = dailyService.findAllDailies(testUser1.id, today.toLocalDate(), 0, 2)

        // then
        result.currentPage shouldBeEqual 1
        result.isEnd shouldBeEqual true
        result.dailies[0].dailyId shouldBe daily4.id
        result.dailies[1].dailyId shouldBe daily3.id
    }
}
