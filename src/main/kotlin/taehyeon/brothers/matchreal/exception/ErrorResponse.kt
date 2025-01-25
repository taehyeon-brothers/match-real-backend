package taehyeon.brothers.matchreal.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val code: String,
    val message: String,
    val path: String,
    val errors: List<FieldError> = emptyList()
) {
    data class FieldError(
        val field: String,
        val value: Any?,
        val reason: String
    )
} 