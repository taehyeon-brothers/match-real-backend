package taehyeon.brothers.matchreal.presentation.daily.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import java.time.LocalDate
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
import taehyeon.brothers.matchreal.presentation.daily.dto.response.DailyDetailResponse
import taehyeon.brothers.matchreal.presentation.daily.dto.response.DailyUploadResponse
import taehyeon.brothers.matchreal.presentation.daily.dto.response.FeedDailyResponses
import taehyeon.brothers.matchreal.presentation.daily.dto.response.TagDetailResponse

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
    ): ResponseEntity<DailyUploadResponse> {
        val daily = dailyService.uploadDaily(user, dailyImage)
        return ResponseEntity.status(HttpStatus.CREATED).body(DailyUploadResponse(daily.id))
    }

    @GetMapping("/{dailyId}")
    fun getDaily(@PathVariable dailyId: Long): ResponseEntity<DailyDetailResponse> {
        val daily = dailyService.findDailyById(dailyId)
        return ResponseEntity.ok(daily)
    }

    @GetMapping("/{dailyId}/image")
    fun getDailyImage(@PathVariable dailyId: Long): ResponseEntity<Resource> {
        val daily = dailyService.findDailyImageById(dailyId)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(daily.imageContentType))
            .contentLength(daily.imageContent.size.toLong())
            .body(InputStreamResource(ByteArrayResource(daily.imageContent)))
    }

    @GetMapping("/all")
    fun getAll(
        @RequiredLogin user: User,
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) targetDate: LocalDate? = LocalDate.now(),
        @RequestParam page: Int,
        @RequestParam size: Int
    ): ResponseEntity<FeedDailyResponses> {
        val response = dailyService.findAllDailies(userId, targetDate, page - 1, size)
        return ResponseEntity.ok().body(response)
    }

    @PostMapping("/{dailyId}/tag")
    fun addTagByUser(
        @RequiredLogin user: User,
        @PathVariable("dailyId") dailyId: Long,
        @RequestBody tagAddRequest: TagAddRequest,
    ): ResponseEntity<TagDetailResponse> {
        val savedTagId = tagService.addTagByUser(dailyId, tagAddRequest.tagName)
        return ResponseEntity.status(HttpStatus.CREATED).body(TagDetailResponse(savedTagId, tagAddRequest.tagName))
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
