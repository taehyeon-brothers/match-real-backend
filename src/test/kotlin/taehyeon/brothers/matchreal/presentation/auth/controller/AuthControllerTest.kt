package taehyeon.brothers.matchreal.presentation.auth.controller

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import taehyeon.brothers.matchreal.domain.auth.JwtTokenProvider
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.infrastructure.auth.client.GoogleOAuthClient
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.presentation.auth.dto.request.AuthorizationCodeRequest
import taehyeon.brothers.matchreal.presentation.auth.dto.request.RefreshTokenRequest
import taehyeon.brothers.matchreal.support.fixture.UserFixture
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var googleOAuthClient: GoogleOAuthClient

    override suspend fun beforeTest(testCase: TestCase) {
        userRepository.deleteAll()
        clearAllMocks()
    }

    init {
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

                    every { googleOAuthClient.generateAccessToken(code, redirectUri) } returns accessToken
                    every { googleOAuthClient.getUserInfo(accessToken) } returns newUser

                    // when & then
                    mockMvc.perform(
                        post("/api/v1/login/google")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.accessToken").exists())
                        .andExpect(jsonPath("$.refreshToken").exists())

                    verify(exactly = 1) { 
                        googleOAuthClient.generateAccessToken(code, redirectUri)
                        googleOAuthClient.getUserInfo(accessToken)
                    }
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
                    // given
                    val request = RefreshTokenRequest(testRefreshToken!!)

                    // when & then
                    mockMvc.perform(
                        post("/api/v1/login/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.accessToken").exists())
                        .andExpect(jsonPath("$.refreshToken").exists())
                }
            }

            context("잘못된 리프레시 토큰으로 요청하면") {
                it("새로운 액세스 토큰을 발급받을 수 없다") {
                    // given
                    val request = RefreshTokenRequest("invalid-refresh-token")

                    // when & then
                    mockMvc.perform(
                        post("/api/v1/login/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().isUnauthorized)
                        .andExpect(jsonPath("$.message").exists())
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

            context("유효한 액세스 토큰으로 요청하면") {
                it("리프레시 토큰이 제거된다") {
                    // given
                    val accessToken = jwtTokenProvider.createAccessToken(testUser!!)

                    // when & then
                    mockMvc.perform(
                        delete("/api/v1/logout")
                            .header("Authorization", "Bearer $accessToken")
                    )
                        .andExpect(status().isNoContent)

                    // verify
                    val updatedUser = userRepository.findById(testUser!!.id!!).get()
                    assert(updatedUser.refreshToken == null)
                }
            }

            context("잘못된 액세스 토큰으로 요청하면") {
                it("로그아웃할 수 없다") {
                    // when & then
                    mockMvc.perform(
                        delete("/api/v1/logout")
                            .header("Authorization", "Bearer invalid-access-token")
                    )
                        .andExpect(status().isUnauthorized)
                        .andExpect(jsonPath("$.message").exists())
                }
            }
        }
    }
}
