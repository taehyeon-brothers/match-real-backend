package taehyeon.brothers.matchreal.presentation.auth.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import taehyeon.brothers.matchreal.application.auth.service.AuthService
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.presentation.auth.dto.request.AuthorizationCodeRequest
import taehyeon.brothers.matchreal.presentation.auth.dto.request.RefreshTokenRequest
import taehyeon.brothers.matchreal.presentation.auth.dto.response.TokenResponse
import taehyeon.brothers.matchreal.presentation.argumentresolver.RequiredLogin

@RestController
@RequestMapping("/api/v1")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login/google")
    fun login(
        @RequestBody request: AuthorizationCodeRequest
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.googleLogin(request))
    }

    @PostMapping("/login/refresh")
    fun refreshToken(
        @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<TokenResponse> {
        val tokenResponse = authService.refreshAccessToken(request.refreshToken)
        return ResponseEntity.ok(tokenResponse)
    }

    @DeleteMapping("/logout")
    @SecurityRequirement(name = "JWT")
    fun logout(@RequiredLogin user: User): ResponseEntity<Unit> {
        authService.logout(user)
        return ResponseEntity.noContent().build()
    }
}
