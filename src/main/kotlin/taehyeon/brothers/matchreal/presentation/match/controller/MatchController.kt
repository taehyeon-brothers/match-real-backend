package taehyeon.brothers.matchreal.presentation.match.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import taehyeon.brothers.matchreal.application.match.MatchService
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.presentation.argumentresolver.RequiredLogin
import taehyeon.brothers.matchreal.presentation.match.dto.response.MatchResponse

@RestController
@RequestMapping("/api/v1/matches")
@SecurityRequirement(name = "JWT")
class MatchController(
    private val matchService: MatchService
) {
    @Operation(
        summary = "매칭 API",
        description = "요청한 사용자와 일상의 결이 맞는 최대 3명의 사용자를 매칭해줍니다",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "성공적으로 매칭됨",
                content = [Content(schema = Schema(implementation = MatchResponse::class))]
            )
        ]
    )
    @PostMapping
    fun match(@RequiredLogin user: User): ResponseEntity<MatchResponse> =
        ResponseEntity.ok(matchService.matchUsers(user))
}
