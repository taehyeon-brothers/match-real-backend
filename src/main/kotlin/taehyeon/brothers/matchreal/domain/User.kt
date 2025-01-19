package taehyeon.brothers.matchreal.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var nickname: String,

    @Column(unique = true)
    var email: String,

    var profileImageUrl: String? = null,

    @Column(nullable = false)
    var oauthId: String,

    @Column(nullable = false)
    var oauthProvider: String,

    var refreshToken: String? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
