package taehyeon.brothers.matchreal.infrastructure.ai.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ClaudeSendMessageResponse(
    val content: List<Content>
) {
    data class Content(
        val text: String
    )

    fun getResponseText(): String {
        return content.firstOrNull()?.text ?: ""
    }
}
