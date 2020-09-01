package it.unibo.coordination.tusow.api

import io.vertx.core.Promise
import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.tusow.presentation.Link
import it.unibo.coordination.tusow.presentation.User

interface UsersApi : Api {
    fun createUser(userData: User, promise: Promise<Link>)
    fun readAllUsers(skip: Int, limit: Int, filter: String, promise: Promise<Collection<User>>)
    fun readUser(identifier: String, promise: Promise<User>)
    fun updateUser(identifier: String, newUserData: User, promise: Promise<User>)

    companion object {
        @Suppress("UNUSED_PARAMETER")
        @JvmStatic
        operator fun get(context: RoutingContext): UsersApi {
            throw UnsupportedOperationException("not implemented")
        }
    }
}