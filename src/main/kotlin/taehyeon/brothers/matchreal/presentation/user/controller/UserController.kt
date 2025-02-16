package taehyeon.brothers.matchreal.presentation.user.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import taehyeon.brothers.matchreal.domain.user.User
import taehyeon.brothers.matchreal.application.user.UserService
import taehyeon.brothers.matchreal.presentation.argumentresolver.RequiredLogin
import taehyeon.brothers.matchreal.presentation.user.dto.UserResponse

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun getProfile(@RequiredLogin user: User): UserResponse {
        val foundUser = userService.getUser(user.id)
        return UserResponse.from(foundUser)
    }
}
