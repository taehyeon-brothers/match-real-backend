package taehyeon.brothers.matchreal.exception.handler

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import taehyeon.brothers.matchreal.exception.ErrorCode
import taehyeon.brothers.matchreal.exception.ErrorResponse
import taehyeon.brothers.matchreal.exception.base.BusinessException
import taehyeon.brothers.matchreal.exception.base.ClientException
import taehyeon.brothers.matchreal.exception.base.DatabaseException
import taehyeon.brothers.matchreal.exception.base.NetworkException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ApiResponses(
        ApiResponse(
            responseCode = "4xx",
            description = "네트워크 관련 예외 발생",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @ExceptionHandler(NetworkException::class)
    fun handleNetworkException(e: NetworkException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse(
                code = e.errorCode.code,
                message = e.message,
                path = request.requestURI
            ))
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "4xx or 5xx",
            description = "데이터베이스 관련 예외 발생",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @ExceptionHandler(DatabaseException::class)
    fun handleDatabaseException(e: DatabaseException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        if (e.errorCode.status.is5xxServerError) {
            log.error("Database Error: ${e.message}", e)
        }
        
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse(
                code = e.errorCode.code,
                message = e.message,
                path = request.requestURI
            ))
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "4xx or 5xx",
            description = "외부 API 관련 예외 발생",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @ExceptionHandler(ClientException::class)
    fun handleClientException(e: ClientException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        if (e.errorCode.status.is5xxServerError) {
            log.error("External API Error: ${e.message}", e)
        }

        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse(
                code = e.errorCode.code,
                message = e.message,
                path = request.requestURI
            ))
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "4xx or 5xx",
            description = "비즈니스 로직 관련 예외 발생",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        if (e.errorCode.status.is5xxServerError) {
            log.error("Business Error: ${e.message}", e)
        }
        
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse(
                code = e.errorCode.code,
                message = e.message,
                path = request.requestURI
            ))
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "400",
            description = "입력값 검증 실패",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errors = e.bindingResult.fieldErrors.map {
            ErrorResponse.FieldError(
                field = it.field,
                value = it.rejectedValue,
                reason = it.defaultMessage ?: "알 수 없는 오류"
            )
        }

        return ResponseEntity
            .badRequest()
            .body(ErrorResponse(
                code = ErrorCode.INVALID_INPUT_VALUE.code,
                message = ErrorCode.INVALID_INPUT_VALUE.message,
                path = request.requestURI,
                errors = errors
            ))
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "5xx",
            description = "서버 내부 오류",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected Error: ${e.message}", e)
        
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse(
                code = ErrorCode.INTERNAL_SERVER_ERROR.code,
                message = "서버 내부 오류가 발생했습니다",
                path = request.requestURI
            ))
    }
}
