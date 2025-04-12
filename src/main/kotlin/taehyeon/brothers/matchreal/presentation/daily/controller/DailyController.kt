package taehyeon.brothers.matchreal.presentation.daily.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
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
import taehyeon.brothers.matchreal.presentation.daily.dto.response.DailyDetailResponse
import taehyeon.brothers.matchreal.presentation.daily.dto.response.DailyUploadResponse
import taehyeon.brothers.matchreal.presentation.daily.dto.response.FeedDailyResponses

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
        val dailyDetailResponse = dailyService.findDailyById(dailyId)
        return ResponseEntity.ok().body(dailyDetailResponse)
    }

    @GetMapping("/all")
    fun getAll(
        @RequiredLogin user: User,
        @RequestParam page: Int,
        @RequestParam size: Int
    ): ResponseEntity<FeedDailyResponses> {
        val response = dailyService.findAllDailies(page - 1, size)
        return ResponseEntity.ok().body(response)
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
