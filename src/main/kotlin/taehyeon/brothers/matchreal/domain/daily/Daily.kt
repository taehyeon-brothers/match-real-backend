package taehyeon.brothers.matchreal.domain.daily

import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import taehyeon.brothers.matchreal.domain.user.User
import java.time.LocalDateTime

@Entity
@Table(name = "diaries")
class Daily(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var user: User,

    @Column(name = "image_url", length = 200, nullable = false)
    var imageUrl: String,

    @Column(name = "created_at", length = 40, nullable = false)
    var createdAt: LocalDateTime,
)
