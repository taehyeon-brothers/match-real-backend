package taehyeon.brothers.matchreal.support.fixture

import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.user.User

object DailyFixture {
    fun create(
        id: Long = 0L,
        user: User,
        imageUrl: String = "testImg.img",
    ): Daily = Daily(
        id = id,
        user = user,
        imageUrl = imageUrl,
    )

    fun createDailyImage(
        name: String = "클라이밍",
        fileName: String = "test_img.jpg",
        contentType: String = MediaType.MULTIPART_FORM_DATA_VALUE,
        content: String = "file",
    ): MockMultipartFile = MockMultipartFile(
        name,
        fileName,
        contentType,
        content.toByteArray()
    )
}
