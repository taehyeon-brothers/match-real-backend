package taehyeon.brothers.matchreal.presentation.daily.dto.response

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import taehyeon.brothers.matchreal.domain.daily.Daily

data class DailyUploadResponse(
    val dailyId: Long,
)

data class FeedDailyResponses(
    val currentPage: Int,
    val isEnd: Boolean,
    val dailies: List<FeedDailyResponse>,
) {
    companion object {
        fun of(currentPage: Int, totalPage: Int, feedRawDailyResponses: List<FeedRawDailyResponse>): FeedDailyResponses {
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
    val dailyImage: Resource,
    val userId: Long,
    val userNickname: String,
) {
    companion object {
        fun from(feedRawDailyResponse: FeedRawDailyResponse): FeedDailyResponse {
            return FeedDailyResponse(
                dailyId = feedRawDailyResponse.dailyId,
                dailyImage = InputStreamResource(ByteArrayResource(feedRawDailyResponse.imageContent)),
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
