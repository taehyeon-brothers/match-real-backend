package taehyeon.brothers.matchreal.infrastructure.ai.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ClaudeSendMessageRequest(
    val model: String,
    val messages: List<Message>,
    val maxTokens: Int = 1024,
    val temperature: Double = 0.7
) {
    data class Message(
        val role: String = "user",
        val content: String
    )

    companion object {
        fun createPrompt(content: String): Message {
            return Message(content = content)
        }
    }
}
