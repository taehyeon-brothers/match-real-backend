package taehyeon.brothers.matchreal.domain.tag

import jakarta.persistence.*
import taehyeon.brothers.matchreal.domain.daily.Daily
import java.time.LocalDateTime
import taehyeon.brothers.matchreal.domain.common.BaseTimeEntity
import taehyeon.brothers.matchreal.domain.user.User

@Entity
@Table(name = "tags")
class Tag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var daily: Daily,

    @Column(length = 50)
    var tagName: String,
): BaseTimeEntity() {

    companion object {
        fun createForm(
            daily: Daily,
            tagName: String,
        ) = Tag(
            daily = daily,
            tagName = tagName,
        )
    }
}
