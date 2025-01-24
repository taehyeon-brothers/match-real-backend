package taehyeon.brothers.matchreal.presentation.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import taehyeon.brothers.matchreal.presentation.argumentresolver.LoginUserArgumentResolver
import taehyeon.brothers.matchreal.presentation.interceptor.LoginInterceptor

@Configuration
class WebConfig(
    private val loginUserArgumentResolver: LoginUserArgumentResolver,
    private val loginInterceptor: LoginInterceptor
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(loginUserArgumentResolver)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loginInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/**/login/**")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowCredentials(true)
            .allowedMethods("*")
    }
} 