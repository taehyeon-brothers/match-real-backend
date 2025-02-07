package taehyeon.brothers.matchreal.application.daily.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearAllMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import taehyeon.brothers.matchreal.domain.auth.OAuthProvider
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.infrastructure.daily.repository.DailyRepository

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DailyServiceTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var dailyService: DailyService

    @Autowired
    private lateinit var dailyRepository: DailyRepository

    init {
        this.beforeTest {
            dailyRepository.deleteAll()
            clearAllMocks()
        }

        /**
         * TODO: 테스트코드 작성
         */
        describe("데일리 업로드") {
            context("13-19시 사이에 데일리를 업로드하면") {
                it("정상적으로 데일리가 업로드된다") {
                    val user = User.createFrom("케이", "email@naver.com", "oauthId", OAuthProvider.GOOGLE, null)
                    dailyService.uploadDaily(
                        user,
                        MockMultipartFile(
                            "클라이밍",
                            "test_img.jpg",
                            MediaType.MULTIPART_FORM_DATA_VALUE,
                            "file".toByteArray()
                        )
                    )
                }
            }

            context("데일리를 이미 업로드한 유저가 추가적으로 데일리를 업로드하면") {
                it("정상적으로 데일리가 업로드된다") {

                }
            }

            context("13-19시 사이가 아닌 시간대에 데일리를 업로드하면") {

            }

            context("로그인하지 않은 유저가 데일리를 업로드하면") {

            }
        }
    }
}
