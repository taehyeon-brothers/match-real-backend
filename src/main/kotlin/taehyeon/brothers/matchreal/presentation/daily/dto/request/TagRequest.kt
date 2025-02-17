package taehyeon.brothers.matchreal.presentation.daily.dto.request

data class TagAddRequest(
    val tagName: String,
)

data class TagRemoveRequest(
    val tagId: Long,
)
