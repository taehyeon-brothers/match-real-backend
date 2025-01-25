package taehyeon.brothers.matchreal.exception.client

import taehyeon.brothers.matchreal.exception.ErrorCode
import taehyeon.brothers.matchreal.exception.base.ClientException

class ExternalApiException(
    override val errorCode: ErrorCode = ErrorCode.EXTERNAL_API_ERROR,
    override val message: String = errorCode.message,
    val isTimeout: Boolean = false
) : ClientException(errorCode, message) 