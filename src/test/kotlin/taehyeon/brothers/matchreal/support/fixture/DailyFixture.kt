package taehyeon.brothers.matchreal.support.fixture

import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import taehyeon.brothers.matchreal.domain.daily.Daily
import taehyeon.brothers.matchreal.domain.tag.Tag
import taehyeon.brothers.matchreal.domain.user.User

object DailyFixture {
    fun create(
        id: Long = 0L,
        user: User,
        imageName: String = "클라이밍",
        imageContentType: String = MediaType.IMAGE_JPEG.type,
        imageContent: ByteArray = "testImg".toByteArray(),
    ): Daily = Daily(
        id = id,
        user = user,
        imageName = imageName,
        imageContentType = imageContentType,
        imageContent = imageContent,
    )

    fun createDailyImage(
        name: String = "test",
        fileName: String = "클라이밍",
        contentType: String = MediaType.IMAGE_JPEG.type,
        content: String = "testImg",
    ): MockMultipartFile = MockMultipartFile(
        name,
        fileName,
        contentType,
        content.toByteArray()
    )

    fun createTag(
        id: Long = 0L,
        daily: Daily,
        tagName: String = "제이온"
    ): Tag = Tag(
        id = id,
        daily = daily,
        tagName = tagName
    )
}
