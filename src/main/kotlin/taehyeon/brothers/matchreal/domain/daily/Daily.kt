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
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcType
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType
import taehyeon.brothers.matchreal.domain.common.BaseTimeEntity
import taehyeon.brothers.matchreal.domain.user.User

@Entity
@Table(name = "dailies")
class Daily(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var user: User,

    @Column(name = "image_name", length = 200, nullable = false)
    val imageName: String,

    @Column(name = "image_content_type", length = 80, nullable = false)
    val imageContentType: String,

    @Lob
    @JdbcType(value = VarbinaryJdbcType::class)
    @Column(columnDefinition = "bytea", name = "image_content", nullable = false)
    var imageContent: ByteArray,
) : BaseTimeEntity() {

    companion object {
        fun createForm(
            user: User,
            imageName: String,
            imageContentType: String,
            imageContent: ByteArray
        ) = Daily(
            user = user,
            imageName = imageName,
            imageContentType = imageContentType,
            imageContent = imageContent
        )
    }
}
