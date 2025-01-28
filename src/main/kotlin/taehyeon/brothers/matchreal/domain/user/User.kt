package taehyeon.brothers.matchreal.domain.user

import taehyeon.brothers.matchreal.domain.auth.OAuthProvider
import taehyeon.brothers.matchreal.domain.common.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDateTime

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

    @ElementCollection
    @CollectionTable(
        name = "user_tags",
        joinColumns = [JoinColumn(name = "user_id")]
    )
    var tags: MutableList<String> = mutableListOf(),

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
