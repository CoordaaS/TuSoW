package it.unibo.coordination.tusow.exceptions

open class HttpError : RuntimeException {
    val statusCode: Int

    constructor(statusCode: Int) {
        this.statusCode = statusCode
    }

    constructor(statusCode: Int, message: String?) : super(message) {
        this.statusCode = statusCode
    }

    constructor(statusCode: Int, message: String?, cause: Throwable?) : super(message, cause) {
        this.statusCode = statusCode
    }

}