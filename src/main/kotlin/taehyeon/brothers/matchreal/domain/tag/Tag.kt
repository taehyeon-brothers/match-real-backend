package taehyeon.brothers.matchreal.domain.tag

import jakarta.persistence.*
import taehyeon.brothers.matchreal.domain.daily.Daily
import java.time.LocalDateTime

@Entity
@Table(name = "tags")
class Tag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var daily: Daily,

    @Column(name = "tag_name", length = 50)
    var tagName: String,

    @Column(name = "created_at", length = 40, nullable = false)
    var createdAt: LocalDateTime,
)
