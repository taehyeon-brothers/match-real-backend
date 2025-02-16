package taehyeon.brothers.matchreal.application.daily.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import taehyeon.brothers.matchreal.domain.auth.JwtTokenProvider
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.business.DailyUploadTimeException
import taehyeon.brothers.matchreal.infrastructure.common.LocalDateTimeHelper
import taehyeon.brothers.matchreal.infrastructure.daily.repository.DailyRepository
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.support.fixture.DailyFixture
import taehyeon.brothers.matchreal.support.fixture.UserFixture

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DailyServiceTest {

    @Autowired
    private lateinit var dailyRepository: DailyRepository

    @Autowired
    private lateinit var dailyService: DailyService

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: User
    private lateinit var testRefreshToken: String

    @BeforeEach
    fun setUp() {
        LocalDateTimeHelper.fixCurrentTime(LocalDateTime.of(2025, 2, 16, 13, 30, 0))
        val user = userRepository.save(UserFixture.create())
        testRefreshToken = jwtTokenProvider.createRefreshToken(user)
        user.updateRefreshToken(testRefreshToken)
        testUser = userRepository.save(user)
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
        val daily = dailyService.uploadDaily(testUser, dailyImage)

        // then
        assertThat(daily).isNotNull()
        daily.user shouldBe testUser
        daily.imageName shouldBe "클라이밍"
        daily.imageContentType shouldBe MediaType.IMAGE_JPEG.type
        daily.imageContent shouldBe "testImg".toByteArray()
    }

    @Test
    @DisplayName("데일리 업로드 - 이미 업로드를 한 케이스도 다른 이미지 업로드 시 정상으로 처리")
    fun dailyUploadDuplicate() {
        // given
        val dailyImage = DailyFixture.createDailyImage()

        // when
        val daily = dailyService.uploadDaily(testUser, dailyImage)

        // then
        assertThat(daily).isNotNull()
    }

    @Test
    @DisplayName("데일리 업로드 - 13~19시 이외 시간대 업로드 시 예외 반환")
    fun dailyUploadTimeException() {
        // given
        LocalDateTimeHelper.fixCurrentTime(LocalDateTime.of(2025, 2, 16, 10, 30, 0))
        val dailyImage = DailyFixture.createDailyImage()

        // when
        // then
        shouldThrow<DailyUploadTimeException> {
            dailyService.uploadDaily(testUser, dailyImage)
        }
    }

    @Test
    @DisplayName("데일리 조회")
    fun findDailyById() {
        // given
        val daily = dailyRepository.save(DailyFixture.create(user = testUser))

        // when
        val result = dailyService.findDailyById(dailyId = daily.id)

        // then
        result.id shouldBeEqual daily.id
        result.imageContentType shouldBe MediaType.IMAGE_JPEG.type
        result.imageName shouldBeEqual "클라이밍"
    }
}
