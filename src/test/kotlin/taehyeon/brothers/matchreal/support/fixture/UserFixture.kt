package taehyeon.brothers.matchreal.support.fixture

import taehyeon.brothers.matchreal.domain.auth.OAuthProvider
import taehyeon.brothers.matchreal.domain.user.Gender
import taehyeon.brothers.matchreal.domain.user.User

object UserFixture {
    fun create(
        id: Long = 0L,
        nickname: String = "Test User",
        email: String = "test@example.com",
        gender: Gender = Gender.MALE,
        oauthId: String = "test-oauth-id",
        oauthProvider: OAuthProvider = OAuthProvider.GOOGLE,
        profileImageUrl: String? = "http://example.com/profile.jpg"
    ): User = User(
        id = id,
        nickname = nickname,
        email = email,
        gender = gender,
        oauthId = oauthId,
        oauthProvider = oauthProvider,
        profileImageUrl = profileImageUrl
    )
}
