package taehyeon.brothers.matchreal.presentation.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import taehyeon.brothers.matchreal.application.auth.service.AuthService
import taehyeon.brothers.matchreal.presentation.argumentresolver.LoginUserArgumentResolver

@Configuration
class AuthenticationPrincipalConfig(
    private val authService: AuthService
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(loginUserArgumentResolver())
    }

    @Bean
    fun loginUserArgumentResolver(): LoginUserArgumentResolver {
        return LoginUserArgumentResolver(authService)
    }
} 