package taehyeon.brothers.matchreal.infrastructure.ai.client

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import taehyeon.brothers.matchreal.infrastructure.ai.client.dto.ClaudeSendMessageRequest
import taehyeon.brothers.matchreal.infrastructure.ai.client.dto.ClaudeSendMessageResponse
import taehyeon.brothers.matchreal.infrastructure.common.HttpClient
import java.util.Base64

@Component
class ClaudeClient(
    okHttpClient: OkHttpClient,
    objectMapper: ObjectMapper,
    @Value("\${ai.claude.api-key}") private val apiKey: String,
    @Value("\${ai.claude.base-url}") private val baseUrl: String,
    @Value("\${ai.claude.model}") private val model: String,
    @Value("\${ai.claude.version}") private val version: String
) : HttpClient(okHttpClient, objectMapper) {

    fun chat(prompt: String): String {
        val request = ClaudeSendMessageRequest(
            model = model,
            messages = listOf(ClaudeSendMessageRequest.createPrompt(prompt))
        )

        val requestBody = objectMapper.writeValueAsString(request)
            .toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("$baseUrl/messages")
            .post(requestBody)
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", version)
            .build()

        return executeRequest<ClaudeSendMessageResponse>(httpRequest).getResponseText()
    }

    fun chatWithImage(prompt: String, imageContent: ByteArray, contentType: String): String {
        val imageBase64 = Base64.getEncoder().encodeToString(imageContent)

        val request = ClaudeSendMessageRequest(
            model = model,
            messages = listOf(
                ClaudeSendMessageRequest.createPromptWithImage(
                    textContent = prompt,
                    mediaType = contentType,
                    base64Image = imageBase64
                )
            )
        )

        val requestBody = objectMapper.writeValueAsString(request)
            .toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("$baseUrl/messages")
            .post(requestBody)
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", version)
            .build()

        return executeRequest<ClaudeSendMessageResponse>(httpRequest).getResponseText()
    }
}
