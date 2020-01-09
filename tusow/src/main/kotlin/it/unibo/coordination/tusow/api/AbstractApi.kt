package it.unibo.coordination.tusow.api

import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.tusow.exceptions.ForbiddenError
import it.unibo.coordination.tusow.exceptions.UnauthorizedError
import it.unibo.coordination.tusow.presentation.User
import java.util.*

internal abstract class AbstractApi(override val routingContext: RoutingContext) : Api {

    protected val authenticatedUser: Optional<User>
        protected get() = Optional.empty()

    protected fun isAuthenticatedUserAtLeast(role: User.Role): Boolean {
        return authenticatedUser.isPresent && authenticatedUser.get().role.compareTo(role) >= 0
    }

    protected fun ensureAuthenticatedUserAtLeast(role: User.Role) {
        if (!authenticatedUser.isPresent) {
            throw UnauthorizedError()
        }
        if (authenticatedUser.get().role.compareTo(role) < 0) {
            throw ForbiddenError()
        }
    }

}