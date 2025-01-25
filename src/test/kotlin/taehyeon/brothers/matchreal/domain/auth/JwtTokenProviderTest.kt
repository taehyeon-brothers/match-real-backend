package taehyeon.brothers.matchreal.domain.auth

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.UnauthorizedException
import taehyeon.brothers.matchreal.domain.auth.OAuthProvider

class JwtTokenProviderTest : DescribeSpec({
    val secretKey = "testsecretkeytestsecretkeytestsecretkeytestsecretkey"
    val accessTokenValidityInMilliseconds = 3600000L
    val refreshTokenValidityInMilliseconds = 1209600000L

    val jwtTokenProvider = JwtTokenProvider(
        accessTokenSecretKey = secretKey,
        refreshTokenSecretKey = secretKey,
        accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds,
        refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds
    )

    val testUser = User(
        id = 1L,
        nickname = "test",
        email = "test@test.com",
        oauthId = "123",
        oauthProvider = OAuthProvider.GOOGLE
    )

    describe("토큰 생성") {
        context("유효한 사용자 정보로") {
            describe("액세스 토큰을 생성할 때") {
                val accessToken = jwtTokenProvider.createAccessToken(testUser)

                it("토큰이 생성되어야 한다") {
                    accessToken shouldNotBe null
                }

                it("토큰이 유효해야 한다") {
                    jwtTokenProvider.validateAccessToken(accessToken)
                }

                it("토큰에서 사용자 ID를 추출할 수 있어야 한다") {
                    val userId = jwtTokenProvider.getAccessTokenPayload(accessToken)
                    userId shouldBe testUser.id.toString()
                }
            }

            describe("리프레시 토큰을 생성할 때") {
                val refreshToken = jwtTokenProvider.createRefreshToken(testUser)

                it("토큰이 생성되어야 한다") {
                    refreshToken shouldNotBe null
                }

                it("토큰이 유효해야 한다") {
                    jwtTokenProvider.validateRefreshToken(refreshToken)
                }
            }
        }
    }

    describe("토큰 검증") {
        context("유효하지 않은 토큰으로") {
            describe("액세스 토큰을 검증할 때") {
                it("예외가 발생해야 한다") {
                    shouldThrow<UnauthorizedException> {
                        jwtTokenProvider.validateAccessToken("invalid.token")
                    }
                }
            }

            describe("리프레시 토큰을 검증할 때") {
                it("예외가 발생해야 한다") {
                    shouldThrow<UnauthorizedException> {
                        jwtTokenProvider.validateRefreshToken("invalid.token")
                    }
                }
            }
        }
    }
}) 