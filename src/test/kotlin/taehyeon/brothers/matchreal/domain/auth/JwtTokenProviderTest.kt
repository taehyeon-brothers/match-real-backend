package taehyeon.brothers.matchreal.domain.auth

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.UnauthorizedException
import java.util.Base64

class JwtTokenProviderTest : DescribeSpec({
    val secretKey = Base64.getEncoder().encodeToString("your-256-bit-secret-your-256-bit-secret-32".toByteArray())
    val accessTokenValidityInMilliseconds = 3600000L
    val refreshTokenValidityInMilliseconds = 1209600000L

    lateinit var jwtTokenProvider: JwtTokenProvider
    lateinit var testUser: User

    beforeTest {
        jwtTokenProvider = JwtTokenProvider(
            accessTokenSecretKey = secretKey,
            refreshTokenSecretKey = secretKey,
            accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds,
            refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds
        )

        testUser = User(
            id = 1L,
            nickname = "test",
            email = "test@test.com",
            oauthId = "123",
            oauthProvider = OAuthProvider.GOOGLE
        )
    }

    describe("JWT 토큰 생성 및 검증") {
        context("액세스 토큰") {
            it("생성된 토큰은 null이 아니어야 한다") {
                val accessToken = jwtTokenProvider.createAccessToken(testUser)
                accessToken shouldNotBe null
            }

            it("생성된 토큰은 검증이 가능해야 한다") {
                val accessToken = jwtTokenProvider.createAccessToken(testUser)
                jwtTokenProvider.validateAccessToken(accessToken)
            }

            it("토큰에서 사용자 ID를 추출할 수 있어야 한다") {
                val accessToken = jwtTokenProvider.createAccessToken(testUser)
                val userId = jwtTokenProvider.getAccessTokenPayload(accessToken)
                userId shouldBe testUser.id.toString()
            }

            it("잘못된 토큰은 UnauthorizedException을 발생시켜야 한다") {
                shouldThrow<UnauthorizedException> {
                    jwtTokenProvider.validateAccessToken("invalid.access.token")
                }
            }
        }

        context("리프레시 토큰") {
            it("생성된 토큰은 null이 아니어야 한다") {
                val refreshToken = jwtTokenProvider.createRefreshToken(testUser)
                refreshToken shouldNotBe null
            }

            it("생성된 토큰은 검증이 가능해야 한다") {
                val refreshToken = jwtTokenProvider.createRefreshToken(testUser)
                jwtTokenProvider.validateRefreshToken(refreshToken)
            }

            it("잘못된 토큰은 UnauthorizedException을 발생시켜야 한다") {
                shouldThrow<UnauthorizedException> {
                    jwtTokenProvider.validateRefreshToken("invalid.refresh.token")
                }
            }
        }

        context("토큰 페이로드 추출") {
            it("잘못된 토큰으로 페이로드 추출 시 UnauthorizedException을 발생시켜야 한다") {
                shouldThrow<UnauthorizedException> {
                    jwtTokenProvider.getAccessTokenPayload("invalid.token")
                }
            }
        }
    }
}) 