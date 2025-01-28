package taehyeon.brothers.matchreal.application.auth.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import taehyeon.brothers.matchreal.domain.auth.JwtTokenProvider
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.database.EntityNotFoundException
import taehyeon.brothers.matchreal.exception.network.UnauthorizedException
import taehyeon.brothers.matchreal.infrastructure.auth.client.GoogleOAuthClient
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.presentation.auth.dto.request.AuthorizationCodeRequest
import taehyeon.brothers.matchreal.support.fixture.UserFixture

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AuthServiceTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    private lateinit var googleOAuthClient: GoogleOAuthClient

    init {
        this.beforeTest {
            userRepository.deleteAll()
        }

        describe("Google 로그인") {
            context("신규 사용자가 로그인을 시도하면") {
                it("회원가입 후 토큰이 발급된다") {
                    // given
                    val code = "test-auth-code"
                    val redirectUri = "http://localhost:8080/callback"
                    val request = AuthorizationCodeRequest(code, redirectUri)
                    val accessToken = "test-access-token"
                    val newUser = UserFixture.create(
                        oauthId = "new-oauth-id",
                        email = "new@example.com",
                        nickname = "New User"
                    )

                    given(googleOAuthClient.generateAccessToken(code, redirectUri))
                        .willReturn(accessToken)
                    given(googleOAuthClient.getUserInfo(accessToken))
                        .willReturn(newUser)

                    // when
                    val response = authService.googleLogin(request)

                    // then
                    response.accessToken.shouldNotBeNull()
                    response.refreshToken.shouldNotBeNull()
                    
                    val savedUser = userRepository.findByOauthId(newUser.oauthId)
                    savedUser.shouldNotBeNull()
                    savedUser.refreshToken.shouldNotBeNull()
                }
            }
        }

        describe("토큰 갱신") {
            var testUser: User? = null
            var testRefreshToken: String? = null

            beforeTest {
                testUser = UserFixture.create()
                testRefreshToken = jwtTokenProvider.createRefreshToken(testUser!!)
                testUser!!.updateRefreshToken(testRefreshToken!!)
                userRepository.save(testUser!!)
            }

            context("유효한 리프레시 토큰으로 요청하면") {
                it("새로운 액세스 토큰을 발급받을 수 있다") {
                    // when
                    val response = authService.refreshAccessToken(testRefreshToken!!)

                    // then
                    response.accessToken.shouldNotBeNull()
                    response.refreshToken shouldBe testRefreshToken
                }
            }

            context("잘못된 리프레시 토큰으로 요청하면") {
                it("UnauthorizedException이 발생한다") {
                    // given
                    val invalidRefreshToken = "invalid-refresh-token"

                    // when & then
                    shouldThrow<UnauthorizedException> {
                        authService.refreshAccessToken(invalidRefreshToken)
                    }
                }
            }

            context("존재하지 않는 리프레시 토큰으로 요청하면") {
                it("EntityNotFoundException이 발생한다") {
                    // given
                    val nonExistentToken = jwtTokenProvider.createRefreshToken(testUser!!)

                    // when & then
                    shouldThrow<EntityNotFoundException> {
                        authService.refreshAccessToken(nonExistentToken)
                    }
                }
            }
        }

        describe("로그아웃") {
            var testUser: User? = null
            var testRefreshToken: String? = null

            beforeTest {
                testUser = UserFixture.create()
                testRefreshToken = jwtTokenProvider.createRefreshToken(testUser!!)
                testUser!!.updateRefreshToken(testRefreshToken!!)
                userRepository.save(testUser!!)
            }

            it("리프레시 토큰이 제거된다") {
                // when
                authService.logout(testUser!!)

                // then
                val updatedUser = userRepository.findById(testUser!!.id!!).get()
                updatedUser.refreshToken.shouldBeNull()
            }
        }

        describe("액세스 토큰으로 사용자 찾기") {
            var testUser: User? = null
            var testRefreshToken: String? = null

            beforeTest {
                testUser = UserFixture.create()
                testRefreshToken = jwtTokenProvider.createRefreshToken(testUser!!)
                testUser!!.updateRefreshToken(testRefreshToken!!)
                userRepository.save(testUser!!)
            }

            context("유효한 액세스 토큰으로 요청하면") {
                it("사용자를 찾을 수 있다") {
                    // given
                    val accessToken = jwtTokenProvider.createAccessToken(testUser!!)

                    // when
                    val foundUser = authService.findUserByAccessToken(accessToken)

                    // then
                    foundUser.shouldNotBeNull()
                    foundUser.id shouldBe testUser!!.id
                }
            }

            context("잘못된 액세스 토큰으로 요청하면") {
                it("UnauthorizedException이 발생한다") {
                    // given
                    val invalidAccessToken = "invalid-access-token"

                    // when & then
                    shouldThrow<UnauthorizedException> {
                        authService.findUserByAccessToken(invalidAccessToken)
                    }
                }
            }
        }
    }
}
