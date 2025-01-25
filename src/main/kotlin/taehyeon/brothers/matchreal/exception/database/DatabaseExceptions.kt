package taehyeon.brothers.matchreal.exception.database

import taehyeon.brothers.matchreal.exception.ErrorCode
import taehyeon.brothers.matchreal.exception.base.DatabaseException

class EntityNotFoundException(
    override val errorCode: ErrorCode = ErrorCode.ENTITY_NOT_FOUND,
    override val message: String = errorCode.message
) : DatabaseException(errorCode, message)

class DuplicateKeyException(
    override val errorCode: ErrorCode = ErrorCode.DUPLICATE_KEY,
    override val message: String = errorCode.message
) : DatabaseException(errorCode, message)

class InvalidDataException(
    override val errorCode: ErrorCode = ErrorCode.INVALID_DATA,
    override val message: String = errorCode.message
) : DatabaseException(errorCode, message)
