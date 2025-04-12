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
        val content: List<Content>
    )

    sealed class Content {
        data class Text(
            val type: String = "text",
            val text: String
        ) : Content()

        data class Image(
            val type: String = "image",
            val source: Source
        ) : Content()
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Source(
        val type: String = "base64",
        val mediaType: String,
        val data: String
    )

    companion object {
        fun createPrompt(content: String): Message {
            return Message(content = listOf(Content.Text(text = content)))
        }

        fun createPromptWithImage(textContent: String, mediaType: String, base64Image: String): Message {
            return Message(
                content = listOf(
                    Content.Text(text = textContent),
                    Content.Image(
                        source = Source(
                            mediaType = mediaType,
                            data = base64Image
                        )
                    )
                )
            )
        }
    }
}
