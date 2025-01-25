package taehyeon.brothers.matchreal.exception.business

import taehyeon.brothers.matchreal.exception.ErrorCode
import taehyeon.brothers.matchreal.exception.base.BusinessException

class InvalidOperationException(
    override val errorCode: ErrorCode = ErrorCode.INVALID_OPERATION,
    override val message: String = errorCode.message
) : BusinessException(errorCode, message)

class InvalidStateException(
    override val errorCode: ErrorCode = ErrorCode.INVALID_STATE,
    override val message: String = errorCode.message
) : BusinessException(errorCode, message) 