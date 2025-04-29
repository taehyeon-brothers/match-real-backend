package taehyeon.brothers.matchreal.support.fixture

import java.lang.reflect.Field
import java.time.LocalDateTime
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

    fun createWithBaseTime(
        id: Long = 0L,
        user: User,
        imageName: String = "클라이밍",
        imageContentType: String = MediaType.IMAGE_JPEG.type,
        imageContent: ByteArray = "testImg".toByteArray(),
        createdAt: LocalDateTime = LocalDateTime.now()
    ): Daily {
        val daily = Daily(
            id = id,
            user = user,
            imageName = imageName,
            imageContentType = imageContentType,
            imageContent = imageContent,
        )
        setField<Daily>(daily, "createdAt", createdAt)
        return daily
    }

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

    fun <T> setField(entity: Any, fieldName: String, value: Any) {
        var field: Field? = null
        var clazz: Class<*>? = entity::class.java

        // 상위 클래스까지 탐색
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName)
                break
            } catch (e: NoSuchFieldException) {
                clazz = clazz.superclass
            }
        }

        if (field == null) {
            throw NoSuchFieldException("Field '$fieldName' not found in class hierarchy")
        }

        field.isAccessible = true
        field.set(entity, value)
    }
}
