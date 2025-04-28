package taehyeon.brothers.matchreal.presentation.match.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class MatchResponse(
    @Schema(description = "매칭된 유저 ID 리스트", example = "[11, 50, 100]")
    val matchedUserIds: List<Long>,
    
    @Schema(description = "매칭된 유저 ID와 태그 목록", example = "{ \"11\": [\"태그1\", \"태그2\"], \"50\": [\"태그3\", \"태그4\"] }")
    val matchedUsersWithTags: Map<Long, List<String>>
)
