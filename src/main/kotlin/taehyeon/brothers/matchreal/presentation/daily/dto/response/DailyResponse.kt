package taehyeon.brothers.matchreal.presentation.daily.dto.response

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.tag.Tag
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.presentation.user.dto.response.UserResponse

data class DailyUploadResponse(
    val dailyId: Long,
)

data class DailyDetailResponse(
    val dailyId: Long,
    val user: UserResponse,
    val dailyImage: Resource,
    val tags: List<TagDetailResponse>,
) {
    companion object {
        fun of(daily: Daily, user: User, tags: List<Tag>): DailyDetailResponse {
            return DailyDetailResponse(
                dailyId = daily.id,
                user = UserResponse.from(user),
                dailyImage = InputStreamResource(ByteArrayResource(daily.imageContent)),
                tags = tags.map { TagDetailResponse(it.id, it.tagName) }
            )
        }
    }
}

data class TagDetailResponse(
    val tagId: Long,
    val tagName: String,
)

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
