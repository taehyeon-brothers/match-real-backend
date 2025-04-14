package taehyeon.brothers.matchreal.presentation.daily.dto.response

import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.tag.Tag

data class DailyUploadResponse(
    val dailyId: Long,
)

data class DailyDetailResponse(
    val dailyId: Long,
    val userId: Long,
    val userNickname: String,
    val tags: List<TagDetailResponse> = emptyList(),
) {
    companion object {
        fun of(daily: Daily, tags: List<Tag>): DailyDetailResponse {
            return DailyDetailResponse(
                dailyId = daily.id,
                userId = daily.user.id,
                userNickname = daily.user.nickname,
                tags = tags.map { TagDetailResponse(it.id, it.tagName) }
            )
        }
    }
}

data class FeedDailyResponses(
    val currentPage: Int,
    val isEnd: Boolean,
    val dailies: List<FeedDailyResponse>,
) {
    companion object {
        fun of(
            currentPage: Int,
            totalPage: Int,
            feedRawDailyResponses: List<FeedRawDailyResponse>
        ): FeedDailyResponses {
            return FeedDailyResponses(
                currentPage = currentPage,
                isEnd = currentPage == totalPage,
                dailies = feedRawDailyResponses.map { FeedDailyResponse.from(it) }
            )
        }
    }
}

data class FeedDailyResponse(
    val dailyId: Long,
    val userId: Long,
    val userNickname: String,
) {
    companion object {
        fun from(feedRawDailyResponse: FeedRawDailyResponse): FeedDailyResponse {
            return FeedDailyResponse(
                dailyId = feedRawDailyResponse.dailyId,
                userId = feedRawDailyResponse.userId,
                userNickname = feedRawDailyResponse.userNickname,
            )
        }
    }
}

data class FeedRawDailyResponse(
    val dailyId: Long,
    val imageName: String,
    val imageContentType: String,
    val imageContent: ByteArray,
    val userId: Long,
    val userNickname: String,
) {
    companion object {
        fun from(daily: Daily): FeedRawDailyResponse {
            return FeedRawDailyResponse(
                dailyId = daily.id,
                imageName = daily.imageName,
                imageContentType = daily.imageContentType,
                imageContent = daily.imageContent,
                userId = daily.user.id,
                userNickname = daily.user.nickname,
            )
        }
    }
}
