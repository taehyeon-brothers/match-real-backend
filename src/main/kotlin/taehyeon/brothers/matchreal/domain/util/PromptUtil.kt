package taehyeon.brothers.matchreal.domain.util

import org.springframework.core.io.ClassPathResource
import java.nio.charset.StandardCharsets

object PromptUtil {

    fun readPromptFile(promptFile: String): String {
        val resource = ClassPathResource(promptFile)
        return resource.inputStream.use { inputStream ->
            String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
        }
    }
}
