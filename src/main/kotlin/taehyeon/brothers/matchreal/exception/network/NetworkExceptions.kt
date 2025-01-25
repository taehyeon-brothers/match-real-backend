package taehyeon.brothers.matchreal.exception.network

import taehyeon.brothers.matchreal.exception.ErrorCode
import taehyeon.brothers.matchreal.exception.base.NetworkException

class BadRequestException(
    override val errorCode: ErrorCode = ErrorCode.BAD_REQUEST,
    override val message: String = errorCode.message
) : NetworkException(errorCode, message)

class UnauthorizedException(
    override val errorCode: ErrorCode = ErrorCode.UNAUTHORIZED,
    override val message: String = errorCode.message
) : NetworkException(errorCode, message)

class ForbiddenException(
    override val errorCode: ErrorCode = ErrorCode.FORBIDDEN,
    override val message: String = errorCode.message
) : NetworkException(errorCode, message) 