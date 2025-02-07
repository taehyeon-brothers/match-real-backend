package taehyeon.brothers.matchreal.presentation.daily

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import taehyeon.brothers.matchreal.application.daily.service.DailyService
import taehyeon.brothers.matchreal.application.tag.service.TagService
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.presentation.argumentresolver.RequiredLogin

@RestController
@RequestMapping("/api/v1/daily")
class DailyController(
    private val dailyService: DailyService,
    private val tagService: TagService,
) {
    @PostMapping
    @SecurityRequirement(name = "JWT")
    fun uploadDaily(
        @RequiredLogin user: User,
        @RequestParam(value = "file") dailyImage: MultipartFile,
    ) {
        /**
         * TODO: 트랜잭션 성질 보장 필요. 단, tag add 부분은 딥러닝 호출하므로 트랜잭션으로 묶이지 않도록 관리 필요.
         * 트랜잭션 이벤트 리스너를 이용하여 추후 관리하거나, 다른 방법으로 개발 필요.
         */
        val daily = dailyService.uploadDaily(user, dailyImage)
        tagService.addTagsByDailyImage(daily, dailyImage)
    }
}
