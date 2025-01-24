package taehyeon.brothers.matchreal.exception

class UnauthorizedException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) 