package taehyeon.brothers.matchreal.presentation.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class HealthCheckResponse(
    val status: String,
    val message: String
)

@Tag(name = "Health Check")
@RestController
@RequestMapping("/api/health")
class HealthCheckController {

    @Operation(summary = "서버 상태 확인")
    @GetMapping
    fun checkHealth(): ResponseEntity<HealthCheckResponse> {
        return ResponseEntity.ok(
            HealthCheckResponse(
                status = "UP",
                message = "Server is running"
            )
        )
    }
}
