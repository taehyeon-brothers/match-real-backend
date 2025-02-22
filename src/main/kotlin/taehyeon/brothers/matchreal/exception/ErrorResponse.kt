package taehyeon.brothers.matchreal.exception

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "에러 응답 객체")
data class ErrorResponse(
    @Schema(description = "에러 발생 시간")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @Schema(description = "에러 코드")
    val code: String,
    
    @Schema(description = "에러 메시지")
    val message: String,
    
    @Schema(description = "에러가 발생한 요청 경로")
    val path: String,
    
    @Schema(description = "필드 에러 목록")
    val errors: List<FieldError> = emptyList()
) {
    @Schema(description = "필드 에러 정보")
    data class FieldError(
        @Schema(description = "에러가 발생한 필드명")
        val field: String,
        
        @Schema(description = "에러가 발생한 필드의 값")
        val value: Any?,
        
        @Schema(description = "에러 이유")
        val reason: String
    )
}
