package taehyeon.brothers.matchreal.application.daily.service

import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions.assertThat
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
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.support.fixture.DailyFixture
import taehyeon.brothers.matchreal.support.fixture.UserFixture

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DailyServiceTest {

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
        testUser = UserFixture.create()
        testRefreshToken = jwtTokenProvider.createRefreshToken(testUser)
        testUser.updateRefreshToken(testRefreshToken)
        userRepository.save(testUser)
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
        val dailyImage = DailyFixture.createDailyImage()

        // when
        // then
        /**
         * TODO: 테스트 내 시간 관련 커스텀 세팅 작업필요
         * shouldThrow<DailyUploadTimeException> {
         *             dailyService.uploadDaily(testUser, dailyImage)
         *         }
         */
    }
}
