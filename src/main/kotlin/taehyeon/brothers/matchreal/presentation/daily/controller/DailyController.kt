package taehyeon.brothers.matchreal.presentation.daily.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import taehyeon.brothers.matchreal.application.daily.service.DailyService
import taehyeon.brothers.matchreal.application.tag.service.TagService
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.presentation.argumentresolver.RequiredLogin
import taehyeon.brothers.matchreal.presentation.daily.dto.request.TagAddRequest
import taehyeon.brothers.matchreal.presentation.daily.dto.request.TagRemoveRequest
import taehyeon.brothers.matchreal.presentation.daily.dto.response.AddTagResponse

@RestController
@RequestMapping("/api/v1/daily")
@SecurityRequirement(name = "JWT")
class DailyController(
    private val dailyService: DailyService,
    private val tagService: TagService,
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadDaily(
        @RequiredLogin user: User,
        @RequestParam(value = "file") dailyImage: MultipartFile,
    ): ResponseEntity<Resource> {
        /**
         * TODO: 트랜잭션 성질 보장 필요. 단, tag add 부분은 딥러닝 호출하므로 트랜잭션으로 묶이지 않도록 관리 필요.
         * 트랜잭션 이벤트 리스너를 이용하여 추후 관리하거나, 다른 방법으로 개발 필요.
         */
        val daily = dailyService.uploadDaily(user, dailyImage)
        tagService.addTagsByDailyImage(daily, dailyImage)

        return ResponseEntity.status(HttpStatus.CREATED)
            .contentType(MediaType.parseMediaType(daily.imageContentType))
            .contentLength(daily.imageContent.size.toLong())
            .body(InputStreamResource(ByteArrayResource(daily.imageContent)))
    }

    @GetMapping("/{dailyId}")
    fun getDaily(@PathVariable dailyId: Long): ResponseEntity<Resource> {
        val daily = dailyService.findDailyById(dailyId)
        return ResponseEntity.status(HttpStatus.CREATED)
            .contentType(MediaType.parseMediaType(daily.imageContentType))
            .contentLength(daily.imageContent.size.toLong())
            .body(InputStreamResource(ByteArrayResource(daily.imageContent)))
    }

    @PostMapping("/{dailyId}/tag")
    fun addTagByUser(
        @RequiredLogin user: User,
        @PathVariable("dailyId") dailyId: Long,
        @RequestBody tagAddRequest: TagAddRequest,
    ): ResponseEntity<AddTagResponse> {
        val savedTagId = tagService.addTagByUser(dailyId, tagAddRequest.tagName)
        return ResponseEntity.status(HttpStatus.CREATED).body(AddTagResponse(savedTagId, tagAddRequest.tagName))
    }

    @DeleteMapping("/{dailyId}/tag/{tagId}")
    fun removeTagByUser(
        @RequiredLogin user: User,
        @PathVariable("dailyId") dailyId: Long,
        @RequestBody tagRemoveRequest: TagRemoveRequest,
    ): ResponseEntity<Unit> {
        tagService.removeTagByUser(tagRemoveRequest.tagId)
        return ResponseEntity.noContent().build()
    }
}
