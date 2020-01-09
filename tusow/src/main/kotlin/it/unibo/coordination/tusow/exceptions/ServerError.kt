package it.unibo.coordination.tusow.exceptions

open class ServerError : HttpError {
    constructor(statusCode: Int) : super(statusCode)
    constructor(statusCode: Int, message: String?) : super(statusCode, message)
    constructor(statusCode: Int, message: String?, cause: Throwable?) : super(statusCode, message, cause)
}