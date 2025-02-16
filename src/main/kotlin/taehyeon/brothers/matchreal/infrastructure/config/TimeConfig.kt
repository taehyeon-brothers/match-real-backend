package taehyeon.brothers.matchreal.infrastructure.config

import jakarta.annotation.PostConstruct
import java.util.*
import org.springframework.context.annotation.Configuration

@Configuration
class TimeConfig {

    @PostConstruct
    fun timeConfig() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    }
}
