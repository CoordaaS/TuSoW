package it.unibo.coordination.tusow.api

import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext

interface Api {
    val routingContext: RoutingContext

    @JvmDefault
    val authorizationHeader: String
        get() = routingContext.request().getHeader(HttpHeaders.AUTHORIZATION)

    @JvmDefault
    val path: String
        get() {
            val path = routingContext.request().path()
            val queryIndex = path.indexOf('?')
            return path.substring(0, if (queryIndex > 0) queryIndex else path.length)
        }

    @JvmDefault
    fun getPath(subPath: String): String {
        return "$path/$subPath"
    }
}