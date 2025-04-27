package taehyeon.brothers.matchreal.application.match

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.infrastructure.ai.client.ClaudeClient
import taehyeon.brothers.matchreal.infrastructure.tag.repository.TagRepository
import taehyeon.brothers.matchreal.infrastructure.user.repository.UserRepository
import taehyeon.brothers.matchreal.presentation.match.dto.response.MatchResponse
import org.springframework.core.io.ResourceLoader
import java.nio.charset.StandardCharsets
import org.springframework.transaction.annotation.Transactional

private data class MatchInput(
    val users: List<Map<String, Any>>,
    val requester: Map<String, Any>
)

private data class MatchOutput(
    val matchedUserIds: List<Long> = emptyList()
)

@Service
class MatchService(
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository,
    private val claudeClient: ClaudeClient,
    private val objectMapper: ObjectMapper,
    private val resourceLoader: ResourceLoader
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    
    @Transactional(readOnly = true)
    fun matchUsers(requester: User): MatchResponse {
        val userTagsMap = collectUserTags()
        val matchRequestData = prepareMatchRequestData(userTagsMap, requester.id)
        val matchedUserIds = callClaudeForMatching(matchRequestData)
        
        return createMatchResponse(matchedUserIds, userTagsMap)
    }
    
    private fun collectUserTags(): Map<Long, List<String>> {
        val userTags = tagRepository.findAllUserTags()
        return userTags.groupBy { it.userId }
            .mapValues { (_, tags) -> tags.map { it.tagName }.distinct() }
    }
    
    private fun prepareMatchRequestData(
        userTagsMap: Map<Long, List<String>>,
        requesterId: Long
    ): String {
        val users = userRepository.findAll()
            .filter { it.id != requesterId }
            .map { user -> 
                mapOf(
                    "id" to user.id,
                    "tagNames" to (userTagsMap[user.id] ?: emptyList())
                )
            }
            
        val inputData = MatchInput(
            users = users,
            requester = mapOf(
                "id" to requesterId,
                "tagNames" to (userTagsMap[requesterId] ?: emptyList())
            )
        )
        
        return objectMapper.writeValueAsString(inputData)
    }
    
    private fun callClaudeForMatching(matchRequestData: String): List<Long> {
        val promptTemplate = resourceLoader.getResource("classpath:prompt/match.md")
            .inputStream.readAllBytes().toString(StandardCharsets.UTF_8)
        
        val completePrompt = String.format(promptTemplate, matchRequestData)
        
        return try {
            val response = claudeClient.chat(completePrompt)
            val matchOutput = objectMapper.readValue(response, MatchOutput::class.java)
            matchOutput.matchedUserIds
        } catch (e: Exception) {
            log.error("매칭 과정에서 오류 발생: {}", e.message, e)
            emptyList()
        }
    }
    
    private fun createMatchResponse(
        matchedUserIds: List<Long>,
        userTagsMap: Map<Long, List<String>>
    ): MatchResponse {
        val matchedUsersWithTags = matchedUserIds.associateWith { userId ->
            userTagsMap[userId] ?: emptyList()
        }
        
        return MatchResponse(
            matchedUserIds = matchedUserIds,
            matchedUsersWithTags = matchedUsersWithTags
        )
    }
}
