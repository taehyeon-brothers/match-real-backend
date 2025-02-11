package taehyeon.brothers.matchreal.exception.business

import taehyeon.brothers.matchreal.exception.ErrorCode
import taehyeon.brothers.matchreal.exception.base.BusinessException

// 필요할 때 추가 - 비즈니스 로직과 연관된 예외들

class NotFoundImageException(
    override val errorCode: ErrorCode = ErrorCode.IMAGE_URL_ERROR,
    override val message: String = errorCode.message
) : BusinessException(errorCode, message)

class DailyUploadTimeException(
    override val errorCode: ErrorCode = ErrorCode.DAILY_UPLOAD_TIME_ERROR,
    override val message: String = errorCode.message
) : BusinessException(errorCode, message)

