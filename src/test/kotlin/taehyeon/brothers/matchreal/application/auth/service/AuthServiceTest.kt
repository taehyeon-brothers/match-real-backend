package taehyeon.brothers.matchreal.application.auth.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.mockito.BDDMockito.given
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
class AuthServiceTest {
    @Autowired
    private lateinit var authService: AuthService

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
        testUser = UserFixture.create()
        testRefreshToken = jwtTokenProvider.createRefreshToken(testUser)
        testUser.updateRefreshToken(testRefreshToken)
        userRepository.save(testUser)
    }

    @Test
    @DisplayName("Google 로그인 - 신규 사용자")
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

        // when
        val response = authService.googleLogin(request)

        // then
        assertThat(response.accessToken).isNotNull()
        assertThat(response.refreshToken).isNotNull()
        
        val savedUser = userRepository.findByOauthId(newUser.oauthId)
        assertThat(savedUser).isNotNull()
        assertThat(savedUser?.refreshToken).isNotNull()
    }

    @Test
    @DisplayName("액세스 토큰 갱신")
    fun refreshAccessToken() {
        // when
        val response = authService.refreshAccessToken(testRefreshToken)

        // then
        assertThat(response.accessToken).isNotNull()
        assertThat(response.refreshToken).isEqualTo(testRefreshToken)
    }

    @Test
    @DisplayName("잘못된 리프레시 토큰으로 액세스 토큰 갱신 시도")
    fun refreshAccessToken_WithInvalidToken() {
        // given
        val invalidRefreshToken = "invalid-refresh-token"

        // when & then
        assertThrows<UnauthorizedException> {
            authService.refreshAccessToken(invalidRefreshToken)
        }
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰으로 액세스 토큰 갱신 시도")
    fun refreshAccessToken_WithNonExistentToken() {
        // given
        val nonExistentToken = jwtTokenProvider.createRefreshToken(testUser)

        // when & then
        assertThrows<EntityNotFoundException> {
            authService.refreshAccessToken(nonExistentToken)
        }
    }

    @Test
    @DisplayName("로그아웃")
    fun logout() {
        // when
        authService.logout(testUser)

        // then
        val updatedUser = userRepository.findById(testUser.id!!).get()
        assertThat(updatedUser.refreshToken).isNull()
    }

    @Test
    @DisplayName("액세스 토큰으로 사용자 찾기")
    fun findUserByAccessToken() {
        // given
        val accessToken = jwtTokenProvider.createAccessToken(testUser)

        // when
        val foundUser = authService.findUserByAccessToken(accessToken)

        // then
        assertThat(foundUser).isNotNull
        assertThat(foundUser?.id).isEqualTo(testUser.id)
    }

    @Test
    @DisplayName("잘못된 액세스 토큰으로 사용자 찾기")
    fun findUserByAccessToken_WithInvalidToken() {
        // given
        val invalidAccessToken = "invalid-access-token"

        // when & then
        assertThrows<UnauthorizedException> {
            authService.findUserByAccessToken(invalidAccessToken)
        }
    }
}
