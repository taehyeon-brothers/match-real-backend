package taehyeon.brothers.matchreal.presentation.user.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import taehyeon.brothers.matchreal.domain.auth.JwtTokenProvider
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.support.IntegrationTestSupport
import taehyeon.brothers.matchreal.support.fixture.UserFixture

class UserControllerTest : IntegrationTestSupport() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var testUser: User
    private lateinit var accessToken: String

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        testUser = UserFixture.create()
        testUser = userRepository.save(testUser)
        accessToken = jwtTokenProvider.createAccessToken(testUser)
    }

    @Test
    @DisplayName("로그인한 유저는 자신의 프로필을 조회할 수 있다")
    fun getProfile() {
        // when & then
        mockMvc.perform(
            get("/api/v1/users")
                .header("Authorization", "Bearer $accessToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(testUser.id))
            .andExpect(jsonPath("$.nickname").value(testUser.nickname))
            .andExpect(jsonPath("$.email").value(testUser.email))
            .andExpect(jsonPath("$.age").value(testUser.age))
            .andExpect(jsonPath("$.gender").value(testUser.gender.toString()))
            .andExpect(jsonPath("$.introduction").value(testUser.introduction))
            .andExpect(jsonPath("$.profileImageUrl").value(testUser.profileImageUrl))
            .andExpect(jsonPath("$.openChatUrl").value(testUser.openChatUrl))
    }

    @Test
    @DisplayName("토큰이 없으면 프로필 조회에 실패한다")
    fun getProfileWithoutToken() {
        // when & then
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    @DisplayName("잘못된 토큰으로는 프로필 조회에 실패한다")
    fun getProfileWithInvalidToken() {
        // when & then
        mockMvc.perform(
            get("/api/v1/users")
                .header("Authorization", "Bearer invalid-token")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    @DisplayName("존재하지 않는 유저의 토큰으로는 프로필 조회에 실패한다")
    fun getProfileWithNonExistentUser() {
        // given
        val nonExistentUser = UserFixture.create(id = 9999L)
        val tokenForNonExistentUser = jwtTokenProvider.createAccessToken(nonExistentUser)

        // when & then
        mockMvc.perform(
            get("/api/v1/users")
                .header("Authorization", "Bearer $tokenForNonExistentUser")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").exists())
    }
}
