package it.unibo.coordination.tusow.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.presentation.Link;
import it.unibo.coordination.tusow.presentation.User;

import java.util.Collection;

public interface UsersApi extends Api {

    void createUser(User userData, Handler<AsyncResult<Link>> handler);

    void readAllUsers(Integer skip, Integer limit, String filter, Handler<AsyncResult<Collection<? extends User>>> handler);

    void readUser(String identifier, Handler<AsyncResult<User>> handler);

    void updateUser(String identifier, User newUserData, Handler<AsyncResult<User>> handler);

    static UsersApi get(RoutingContext context) {
        throw new UnsupportedOperationException("not implemented");
    }
}