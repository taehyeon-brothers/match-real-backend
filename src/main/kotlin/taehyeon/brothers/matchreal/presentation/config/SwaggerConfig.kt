package taehyeon.brothers.matchreal.presentation.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import taehyeon.brothers.matchreal.presentation.argumentresolver.RequiredLogin

@Configuration
class SwaggerConfig {

    init {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(RequiredLogin::class.java)
    }

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Match Real API")
                .description("Match Real 서비스의 API 문서")
                .version("v1.0.0")
        )
}
