package taehyeon.brothers.matchreal.support.fixture

import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.tag.Tag

object TagFixture {
    fun create(
        id: Long = 0L,
        daily: Daily,
        tagName: String = "정적취미생활",
    ): Tag = Tag(
        id = id,
        daily = daily,
        tagName = tagName,
    )
}
