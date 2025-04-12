package taehyeon.brothers.matchreal.application.tag.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.tag.Tag
import taehyeon.brothers.matchreal.domain.util.PromptUtil
import taehyeon.brothers.matchreal.exception.database.EntityNotFoundException
import taehyeon.brothers.matchreal.infrastructure.ai.client.ClaudeClient
import taehyeon.brothers.matchreal.infrastructure.daily.repository.DailyRepository
import taehyeon.brothers.matchreal.infrastructure.tag.repository.TagRepository

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val dailyRepository: DailyRepository,
    private val claudeClient: ClaudeClient,
) {

    fun addTagsByDailyImage(daily: Daily, dailyImage: MultipartFile): List<Tag> {
        return addTagsByDailyImage(daily, dailyImage.bytes, dailyImage.contentType ?: "image/jpeg")
    }

    fun addTagsByDailyImage(daily: Daily, imageContent: ByteArray, contentType: String): List<Tag> {
        val prompt = PromptUtil.readPromptFile("prompt/extract_image.md")
        
        val aiResponse = claudeClient.chatWithImage(prompt, imageContent, contentType)
        val tags = parseTagsFromResponse(aiResponse, daily)
        
        return tagRepository.saveAll(tags)
    }
    
    /**
     * 응답 형식: #tag1, #tag2, #tag3
     */
    private fun parseTagsFromResponse(response: String, daily: Daily): List<Tag> {
        return response
            .trim()
            .split(", ")
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.startsWith("#") }
            .map { tagText ->
                val tagName = tagText.removePrefix("#").trim()
                Tag.createForm(daily, tagName)
            }
    }

    @Transactional
    fun addTagByUser(dailyId: Long, tagName: String): Long {
        val daily = dailyRepository.findById(dailyId)
            .orElseThrow { EntityNotFoundException(message = "데일리가 존재하지 않습니다. dailyId: $dailyId") }
        val tag = Tag.createForm(daily, tagName)
        val savedTag = tagRepository.save(tag)
        return savedTag.id
    }

    @Transactional
    fun removeTagByUser(tagId: Long) {
        tagRepository.findById(tagId)
            .orElseThrow { EntityNotFoundException(message = "태그가 존재하지 않습니다. tagId: $tagId") }
        tagRepository.deleteById(tagId)
    }
}
