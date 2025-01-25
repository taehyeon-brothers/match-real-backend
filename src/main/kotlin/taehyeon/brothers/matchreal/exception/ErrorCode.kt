package taehyeon.brothers.matchreal.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    // Network Exceptions (400-499)
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "N400", "잘못된 요청입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "N401", "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "N403", "접근이 거부되었습니다"),
    
    // Database Exceptions
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "D404", "엔티티를 찾을 수 없습니다"),
    DUPLICATE_KEY(HttpStatus.CONFLICT, "D409", "중복된 키가 존재합니다"),
    INVALID_DATA(HttpStatus.BAD_REQUEST, "D400", "유효하지 않은 데이터입니다"),
    
    // Client Exceptions
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C501", "외부 API 호출 중 오류가 발생했습니다"),
    
    // Business Exceptions

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C400", "유효하지 않은 입력값입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C500", "서버 내부 오류가 발생했습니다")
}
