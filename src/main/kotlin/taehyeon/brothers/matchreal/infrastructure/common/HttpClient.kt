package taehyeon.brothers.matchreal.infrastructure.common

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import taehyeon.brothers.matchreal.exception.OAuthException

abstract class HttpClient(
    protected val okHttpClient: OkHttpClient,
    protected val objectMapper: ObjectMapper
) {
    protected inline fun <reified T> executeRequest(request: Request): T {
        return okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw OAuthException("API call failed with status code: ${response.code}")
            }
            
            val body = response.body?.string()
            requireNotNull(body) { "Response body cannot be null" }
            objectMapper.readValue(body, T::class.java)
        }
    }

    protected fun buildPostRequest(url: String, body: RequestBody): Request =
        Request.Builder()
            .url(url)
            .post(body)
            .build()

    protected fun buildGetRequest(url: String, headers: Map<String, String> = emptyMap()): Request =
        Request.Builder()
            .url(url)
            .apply { headers.forEach { (key, value) -> addHeader(key, value) } }
            .build()
} 