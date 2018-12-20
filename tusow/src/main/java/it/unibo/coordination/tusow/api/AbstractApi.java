package it.unibo.coordination.tusow.api;

import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.exceptions.ForbiddenError;
import it.unibo.coordination.tusow.exceptions.UnauthorizedError;
import it.unibo.coordination.tusow.presentation.User;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

abstract class AbstractApi implements Api {
    private final RoutingContext routingContext;

    AbstractApi(RoutingContext routingContext) {
        this.routingContext = Objects.requireNonNull(routingContext);
    }

    @Override
    public RoutingContext getRoutingContext() {
        return routingContext;
    }

    protected Optional<User> getAuthenticatedUser() {
        return Optional.empty();
    }

    protected boolean isAuthenticatedUserAtLeast(User.Role role) {
        return getAuthenticatedUser().isPresent() && getAuthenticatedUser().get().getRole().compareTo(role) >= 0;
    }

    protected void ensureAuthenticatedUserAtLeast(User.Role role) {
        if (!getAuthenticatedUser().isPresent()) {
            throw new UnauthorizedError();
        }
        if (getAuthenticatedUser().get().getRole().compareTo(role) < 0) {
            throw new ForbiddenError();
        }
    }
}
