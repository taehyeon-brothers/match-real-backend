package taehyeon.brothers.matchreal.domain.user

import taehyeon.brothers.matchreal.domain.auth.OAuthProvider
import taehyeon.brothers.matchreal.domain.common.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_oauth_id", columnList = "oauthId"),
        Index(name = "idx_email", columnList = "email"),
        Index(name = "idx_refresh_token", columnList = "refreshToken")
    ]
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var nickname: String,

    @Column(nullable = false)
    val email: String,

    var age: Int? = null,

    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,

    @Column(columnDefinition = "TEXT")
    var introduction: String? = null,

    var profileImageUrl: String? = null,

    var openChatUrl: String? = null,

    @Column(nullable = false)
    val oauthId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val oauthProvider: OAuthProvider,

    var refreshToken: String? = null
) : BaseTimeEntity() {

    fun updateRefreshToken(newToken: String?) {
        this.refreshToken = newToken
    }

    fun updateProfile(
        nickname: String? = null,
        gender: Gender? = null,
        age: Int? = null,
        introduction: String? = null,
        openChatUrl: String? = null
    ) {
        nickname?.let { this.nickname = it }
        gender?.let { this.gender = it }
        age?.let { this.age = it }
        introduction?.let { this.introduction = it }
        openChatUrl?.let { this.openChatUrl = it }
    }

    companion object {
        fun createFrom(
            nickname: String,
            email: String,
            oauthId: String,
            oauthProvider: OAuthProvider,
            profileImageUrl: String? = null
        ) = User(
            nickname = nickname,
            email = email,
            oauthId = oauthId,
            oauthProvider = oauthProvider,
            profileImageUrl = profileImageUrl
        )
    }
}
