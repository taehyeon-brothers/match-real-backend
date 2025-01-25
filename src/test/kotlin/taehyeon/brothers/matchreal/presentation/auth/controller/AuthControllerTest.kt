package taehyeon.brothers.matchreal.presentation.auth.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import taehyeon.brothers.matchreal.domain.auth.JwtTokenProvider
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.infrastructure.auth.client.GoogleOAuthClient
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.presentation.auth.dto.request.AuthorizationCodeRequest
import taehyeon.brothers.matchreal.presentation.auth.dto.request.RefreshTokenRequest
import taehyeon.brothers.matchreal.support.IntegrationTestSupport
import taehyeon.brothers.matchreal.support.fixture.UserFixture

class AuthControllerTest : IntegrationTestSupport() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    private lateinit var googleOAuthClient: GoogleOAuthClient

    private lateinit var testUser: User
    private lateinit var testRefreshToken: String

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        testUser = UserFixture.create()
        testRefreshToken = jwtTokenProvider.createRefreshToken(testUser)
        testUser.updateRefreshToken(testRefreshToken)
        userRepository.save(testUser)
    }

    @Test
    @DisplayName("Google 로그인 시 신규 사용자는 회원가입 후 토큰이 발급된다")
    fun googleLogin_NewUser() {
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

        // when & then
        mockMvc.perform(
            post("/api/v1/login/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 새로운 액세스 토큰을 발급받을 수 있다")
    fun refreshToken_Success() {
        // given
        val request = RefreshTokenRequest(testRefreshToken)

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

    @Test
    @DisplayName("잘못된 리프레시 토큰으로는 새로운 액세스 토큰을 발급받을 수 없다")
    fun refreshToken_WithInvalidToken() {
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

    @Test
    @DisplayName("로그아웃 시 리프레시 토큰이 제거된다")
    fun logout_Success() {
        // given
        val accessToken = jwtTokenProvider.createAccessToken(testUser)

        // when & then
        mockMvc.perform(
            delete("/api/v1/logout")
                .header("Authorization", "Bearer $accessToken")
        )
            .andExpect(status().isNoContent)

        // verify
        val updatedUser = userRepository.findById(testUser.id!!).get()
        assert(updatedUser.refreshToken == null)
    }

    @Test
    @DisplayName("잘못된 액세스 토큰으로는 로그아웃할 수 없다")
    fun logout_WithInvalidToken() {
        // when & then
        mockMvc.perform(
            delete("/api/v1/logout")
                .header("Authorization", "Bearer invalid-access-token")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").exists())
    }
}