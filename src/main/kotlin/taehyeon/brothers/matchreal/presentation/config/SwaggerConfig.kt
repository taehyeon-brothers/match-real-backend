package taehyeon.brothers.matchreal.presentation.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import taehyeon.brothers.matchreal.presentation.argumentresolver.RequiredLogin

@Configuration
class SwaggerConfig(
        @Value("\${swagger.server.url}") private val serverUrl: String,
        @Value("\${swagger.server.description}") private val serverDescription: String
) {

    init {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(RequiredLogin::class.java)
    }

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
            .servers(
                    listOf(
                            Server()
                                    .url(serverUrl)
                                    .description(serverDescription)
                    )
            )
            .info(
                    Info()
                            .title("Match Real API")
                            .description("Match Real 서비스의 API 문서")
                            .version("v1.0.0")
            )
            .components(
                    Components()
                            .addSecuritySchemes(
                                    "JWT",
                                    SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                                            .description("JWT 토큰을 입력해주세요.")
                            )
            )
}
