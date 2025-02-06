package taehyeon.brothers.matchreal.infrastructure.auth.client

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import com.fasterxml.jackson.databind.ObjectMapper

class GoogleOAuthClientTest : DescribeSpec({
    val okHttpClient = mockk<OkHttpClient>()
    val call = mockk<Call>()
    val response = mockk<Response>()
    val responseBody = mockk<ResponseBody>()

    val googleOAuthClient = GoogleOAuthClient(
        clientId = "test_client_id",
        clientSecret = "test_client_secret",
        authServerUrl = "https://oauth2.googleapis.com",
        apiServerUrl = "https://www.googleapis.com",
        objectMapper = ObjectMapper(),
        okHttpClient = okHttpClient
    )

    describe("generateAccessToken") {
        context("인증 코드가 주어졌을 때") {
            val code = "test_code"
            val redirectUri = "http://localhost:8080"
            val tokenResponse = """
                {
                    "access_token": "test_access_token",
                    "expires_in": 3600,
                    "scope": "https://www.googleapis.com/auth/userinfo.profile",
                    "token_type": "Bearer"
                }
            """.trimIndent()

            every { okHttpClient.newCall(any()) } returns call
            every { call.execute() } returns response
            every { response.body } returns responseBody
            every { response.isSuccessful } returns true
            every { responseBody.string() } returns tokenResponse
            every { response.close() } returns Unit

            it("구글 액세스 토큰을 반환해야 한다") {
                val result = googleOAuthClient.generateAccessToken(code, redirectUri)
                result shouldBe "test_access_token"
            }
        }
    }

    describe("getUserInfo") {
        context("액세스 토큰이 주어졌을 때") {
            val accessToken = "test_access_token"
            val userInfoResponse = """
                {
                    "id": "123",
                    "email": "test@test.com",
                    "verified_email": true,
                    "name": "Test User",
                    "given_name": "Test",
                    "family_name": "User",
                    "picture": "https://test.com/picture.jpg",
                    "locale": "ko"
                }
            """.trimIndent()

            every { okHttpClient.newCall(any()) } returns call
            every { call.execute() } returns response
            every { response.body } returns responseBody
            every { response.isSuccessful } returns true
            every { responseBody.string() } returns userInfoResponse
            every { response.close() } returns Unit

            it("사용자 정보를 반환해야 한다") {
                val result = googleOAuthClient.getUserInfo(accessToken)
                result.email shouldBe "test@test.com"
                result.oauthId shouldBe "123"
            }
        }
    }
})
