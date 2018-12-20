package it.unibo.coordination.tusow.routes;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.api.RoomsApi;
import it.unibo.coordination.tusow.api.UsersApi;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.HttpError;
import it.unibo.coordination.tusow.presentation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public class UsersPath extends Path {


    public UsersPath() {
        super("/users");
    }

	@Override
	protected void setupRoutes() {
        addRoute(HttpMethod.POST, this::post)
                .consumes(APPLICATION_JSON)
                .consumes(APPLICATION_XML)
                .consumes(APPLICATION_YAML)
                .produces(APPLICATION_JSON)
                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);

        addRoute(HttpMethod.GET, this::get)
                .produces(APPLICATION_JSON)
                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);

        addRoute(HttpMethod.GET, "/:identifier", this::getUser)
                .produces(APPLICATION_JSON)
                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);

        addRoute(HttpMethod.PUT, "/:identifier", this::putUser)
                .consumes(APPLICATION_JSON)
                .consumes(APPLICATION_XML)
                .consumes(APPLICATION_YAML)
                .produces(APPLICATION_JSON)
                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);
	}

	private void post(RoutingContext routingContext) {
		final UsersApi api = UsersApi.get(routingContext);
        final Future<Link> result = Future.future();
        result.setHandler(responseHandler(routingContext));

		try {
			final User user = User.parse(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());
            validateUserForPost(user);
			api.createUser(user, result.completer());
		} catch(HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
			result.fail(new BadContentError(e));
		}
	}

    private void validateUserForPost(User user) {
        requireNoneIsNull(user.getEmail(), user.getUsername(), user.getPassword());
        requireAllAreNull(user.getId(), user.getLink());

        user.setId(UUID.randomUUID());
        user.setLinkUrl(getSubPath(user.getUsername()));
    }

    private void get(RoutingContext routingContext) {
        final UsersApi api = UsersApi.get(routingContext);
        final Future<ListOfUsers> result = Future.future();
        result.setHandler(responseHandler(routingContext, this::cleanUsers));

        try {
            final Optional<Integer> skip = Optional.ofNullable(routingContext.queryParams().get("skip")).map(Integer::parseInt);
            final Optional<Integer> limit = Optional.ofNullable(routingContext.queryParams().get("limit")).map(Integer::parseInt);
            final Optional<String> filter = Optional.ofNullable(routingContext.queryParams().get("filter"));

            api.readAllUsers(skip.orElse(0), limit.orElse(10), filter.orElse(""), result.completer());
        } catch(HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
	}

    private ListOfUsers cleanUsers(ListOfUsers list) {
        return new ListOfUsers(
                list.stream().map(this::cleanUser)
        );
    }


    private void getUser(RoutingContext routingContext) {
        final UsersApi api = UsersApi.get(routingContext);
        final Future<User> result = Future.future();
        result.setHandler(responseHandler(routingContext, this::cleanUser));

        try {
            final String identifier = routingContext.pathParam("identifier");
            api.readUser(identifier, result);
        } catch(HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private void putUser(RoutingContext routingContext) {
        final UsersApi api = UsersApi.get(routingContext);
        final Future<User> result = Future.future();
        result.setHandler(responseHandler(routingContext, this::cleanUser));

        try {
            final User user = User.parse(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());
            validateUserForPutUser(user);

            api.updateUser(routingContext.pathParam("identifier"), user, result.completer());
        } catch(HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException | IOException e) {
            result.fail(new BadContentError(e));
        }
    }

    private User cleanUser(User u) {
        return new User(u).setPassword(null);
    }

    private void validateUserForPutUser(User user) {
        requireAllAreNull(user.getId(), user.getLink(), user.getRole());
    }

}
