package taehyeon.brothers.matchreal.infrastructure.tag.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import taehyeon.brothers.matchreal.domain.tag.Tag

@Repository
interface TagRepository : JpaRepository<Tag, Long> {
}
