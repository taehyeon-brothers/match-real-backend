package taehyeon.brothers.matchreal.exception.base

import taehyeon.brothers.matchreal.exception.ErrorCode

abstract class BaseException(
    open val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)

abstract class NetworkException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : BaseException(errorCode, message)

abstract class DatabaseException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : BaseException(errorCode, message)

abstract class ClientException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : BaseException(errorCode, message)

abstract class BusinessException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : BaseException(errorCode, message) 