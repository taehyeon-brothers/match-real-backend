package taehyeon.brothers.matchreal.presentation.argumentresolver

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import taehyeon.brothers.matchreal.application.auth.service.AuthService
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.exception.UnauthorizedException

@Component
class LoginUserArgumentResolver(
    private val authService: AuthService
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(RequiredLogin::class.java)
                && parameter.parameterType == User::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): User {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw UnauthorizedException("Invalid request")

        val token = extractBearerToken(request)
            ?: throw UnauthorizedException("Authorization header is missing or invalid")

        return authService.findUserByAccessToken(token)
            ?: throw UnauthorizedException("Invalid access token")
    }

    private fun extractBearerToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization") ?: return null
        if (!header.startsWith("Bearer ")) return null
        return header.substring(7)
    }
} 