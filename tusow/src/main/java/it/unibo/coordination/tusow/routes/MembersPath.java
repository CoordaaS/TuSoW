package it.unibo.coordination.tusow.routes;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.api.MembersApi;
import it.unibo.coordination.tusow.api.RoomsApi;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.HttpError;
import it.unibo.coordination.tusow.presentation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public class MembersPath extends Path {


    public MembersPath() {
        super("/members");
    }

	@Override
	protected void setupRoutes() {
        addRoute(HttpMethod.GET, this::get)
                .produces(APPLICATION_JSON)
                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);

        addRoute(HttpMethod.POST, this::post)
                .consumes(APPLICATION_JSON)
                .consumes(APPLICATION_XML)
                .consumes(APPLICATION_YAML)
                .produces(APPLICATION_JSON)
                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);

        addRoute(HttpMethod.DELETE, "/:memberNickname", this::deleteMember);
	}

    private void deleteMember(RoutingContext routingContext) {
        final MembersApi api = MembersApi.get(routingContext);
        final Future<Void> result = Future.future();
        result.setHandler(responseHandlerWithNoContent(routingContext));

        try {
            final String chatRoomName = routingContext.pathParam("chatRoomName");
            final String memberNickname = routingContext.pathParam("memberNickname");

            api.deleteChatRoomMember(chatRoomName, memberNickname, result.completer());
        } catch(HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private void post(RoutingContext routingContext) {
        final MembersApi api = MembersApi.get(routingContext);
        final Future<Link> result = Future.future();
        result.setHandler(responseHandler(routingContext));

		try {
            final String chatRoomName = routingContext.pathParam("chatRoomName");
			final User member = User.parse(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());
            validateMemberForPost(member);

			api.createChatRoomMember(chatRoomName, member, result.completer());
		} catch(HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
			result.fail(new BadContentError(e));
		}
	}

    private void validateMemberForPost(User member) {

    }

    private void get(RoutingContext routingContext) {
        final MembersApi api = MembersApi.get(routingContext);
        final Future<ListOfUsers> result = Future.future();
        result.setHandler(responseHandler(routingContext, this::cleanMembersForGet));

        try {
            final String chatRoomName = routingContext.pathParam("chatRoomName");
            final Optional<Integer> skip = Optional.ofNullable(routingContext.queryParams().get("skip")).map(Integer::parseInt);
            final Optional<Integer> limit = Optional.ofNullable(routingContext.queryParams().get("limit")).map(Integer::parseInt);
            final Optional<String> filter = Optional.ofNullable(routingContext.queryParams().get("filter"));

            api.readChatRoomMembers(chatRoomName, skip.orElse(0), limit.orElse(10), filter.orElse(""), result.completer());
        } catch(HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
	}

    private ListOfUsers cleanMembersForGet(ListOfUsers xs) {
        return new ListOfUsers(
            xs.stream().map(u -> new User(u).setPassword(null))
        );
    }

}
