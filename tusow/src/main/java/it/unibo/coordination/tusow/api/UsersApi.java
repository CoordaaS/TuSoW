package it.unibo.coordination.tusow.api;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.presentation.Link;
import it.unibo.coordination.tusow.presentation.User;

import java.util.Collection;

public interface UsersApi extends Api {

    void createUser(User userData, Promise<Link> promise);

    void readAllUsers(Integer skip, Integer limit, String filter, Promise<Collection<? extends User>> promise);

    void readUser(String identifier, Promise<User> promise);

    void updateUser(String identifier, User newUserData, Promise<User> promise);

    static UsersApi get(RoutingContext context) {
        throw new UnsupportedOperationException("not implemented");
    }
}