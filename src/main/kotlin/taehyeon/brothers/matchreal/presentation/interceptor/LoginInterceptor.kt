package taehyeon.brothers.matchreal.presentation.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import taehyeon.brothers.matchreal.application.auth.service.AuthService
import taehyeon.brothers.matchreal.exception.UnauthorizedException

@Component
class LoginInterceptor(
    private val authService: AuthService
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (HttpMethod.OPTIONS.matches(request.method)) {
            return true
        }

        val accessToken = AuthorizationExtractor.extract(request)
            ?: throw UnauthorizedException("Invalid Access Token")

        if (authService.findUserByAccessToken(accessToken) == null) {
            throw UnauthorizedException("Invalid Access Token: $accessToken")
        }

        return true
    }
} 