package taehyeon.brothers.matchreal.infrastructure.tag.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import taehyeon.brothers.matchreal.domain.tag.Tag

@Repository
interface TagRepository : JpaRepository<Tag, Long> {

    fun findByDailyId(dailyId: Long): List<Tag>
    
    @Query("SELECT u.id as userId, t.tagName as tagName FROM User u JOIN Daily d ON u.id = d.user.id JOIN Tag t ON d.id = t.daily.id")
    fun findAllUserTags(): List<UserTagProjection>
    
    interface UserTagProjection {
        val userId: Long
        val tagName: String
    }
}
