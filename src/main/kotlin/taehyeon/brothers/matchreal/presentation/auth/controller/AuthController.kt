package taehyeon.brothers.matchreal.presentation.auth.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import taehyeon.brothers.matchreal.application.auth.service.AuthService
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.presentation.auth.dto.request.AuthorizationCodeRequest
import taehyeon.brothers.matchreal.presentation.auth.dto.request.RefreshTokenRequest
import taehyeon.brothers.matchreal.presentation.auth.dto.response.TokenResponse
import taehyeon.brothers.matchreal.presentation.argumentresolver.RequiredLogin

@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1")
class AuthController(
    private val authService: AuthService
) {
    @Operation(summary = "구글 로그인")
    @PostMapping("/login/google")
    fun login(
        @RequestBody request: AuthorizationCodeRequest
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.googleLogin(request))
    }

    @Operation(summary = "토큰 갱신")
    @PostMapping("/login/refresh")
    fun refreshToken(
        @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<TokenResponse> {
        val tokenResponse = authService.refreshAccessToken(request.refreshToken)
        return ResponseEntity.ok(tokenResponse)
    }

    @Operation(summary = "로그아웃")
    @DeleteMapping("/logout")
    fun logout(@RequiredLogin user: User): ResponseEntity<Unit> {
        authService.logout(user)
        return ResponseEntity.noContent().build()
    }
}
