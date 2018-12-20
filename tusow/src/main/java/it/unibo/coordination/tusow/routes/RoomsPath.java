package it.unibo.coordination.tusow.routes;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import it.unibo.sd1819.lab6.webchat.api.RoomsApi;
import it.unibo.sd1819.lab6.webchat.exceptions.BadContentError;
import it.unibo.sd1819.lab6.webchat.exceptions.HttpError;
import it.unibo.sd1819.lab6.webchat.exceptions.NotFoundError;
import it.unibo.sd1819.lab6.webchat.presentation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static it.unibo.sd1819.lab6.webchat.presentation.MIMETypes.*;

public class RoomsPath extends Path {


    public RoomsPath() {
        super("/rooms");
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

        addRoute(HttpMethod.GET, "/:chatRoomName", this::getRoom)
                .produces(APPLICATION_JSON)
                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);

        addRoute(HttpMethod.DELETE, "/:chatRoomName", this::deleteRoom);

        append("/:chatRoomName", new MembersPath());
        append("/:chatRoomName", new MessagesPath());
	}

    private void deleteRoom(RoutingContext routingContext) {
        final RoomsApi api = RoomsApi.get(routingContext);
        final Future<Void> result = Future.future();
        result.setHandler(responseHandlerWithNoContent(routingContext));

        try {
            final String chatRoomName = routingContext.pathParam("chatRoomName");
            api.deleteChatRoom(chatRoomName, result.completer());
        } catch(HttpError e) {
            result.fail(e);
        }
    }

    private void getRoom(RoutingContext routingContext) {
        final RoomsApi api = RoomsApi.get(routingContext);
        final Future<ChatRoom> result = Future.future();
        result.setHandler(responseHandler(routingContext, this::cleanChatRoom));

        try {
            final Optional<Integer> limitMessages = Optional.ofNullable(routingContext.queryParams().get("limitMessages")).map(Integer::parseInt);
            final Optional<Integer> limitMembers = Optional.ofNullable(routingContext.queryParams().get("limitMembers")).map(Integer::parseInt);
            final String chatRoomName = routingContext.pathParam("chatRoomName");

            api.readChatRoom(chatRoomName, limitMessages.orElse(10), limitMembers.orElse(10), result.completer());
        } catch(HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private ChatRoom cleanChatRoom(ChatRoom x) {
        return new ChatRoom(x)
                .setOwner(this.cleanMember(x.getOwner()))
                .setMembersFromStream(x.getMembers().stream().map(this::cleanMember))
                .setMessagesFromStream(x.getMessages().stream().map(this::cleanMessage));
    }

    private User cleanMember(User x) {
        return new User()
                .setLink(x.getLink());
    }

    private ChatMessage cleanMessage(ChatMessage x) {
        return new ChatMessage(x)
                .setSender(new User().setLink(x.getSender().getLink()));
    }


    private void post(RoutingContext routingContext) {
		final RoomsApi api = RoomsApi.get(routingContext);
        final Future<Link> result = Future.future();
        result.setHandler(responseHandler(routingContext));

		try {
			final ChatRoom room = ChatRoom.parse(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());
            validateChatRoomForPost(room);
			api.createChatRoom(room, result.completer());
		} catch(HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
			result.fail(new BadContentError(e));
		}
	}

    private void validateChatRoomForPost(ChatRoom room) {
        requireAllAreNull(room.getLink(), room.getMembersCount(), room.getMessagesCount(), room.getOwner());
        requireAllAreNullOrEmpty(room.getMessages());
        requireNoneIsNull(room.getName(), room.getAccessLevel());

        if (room.getMembers() == null) {
            room.setMembers(Collections.emptyList());
        } else {
            try {
                room.setMembersFromStream(room.getMembers().stream().map(this::enrichUser).map(Optional::get));
            } catch (NoSuchElementException e) {
                throw new NotFoundError(e);
            }
        }

        room.setMembersCount(room.getMembers().size());
        room.setMessages(Collections.emptyList());
        room.setMessagesCount(0);
        room.setLinkUrl(getSubPath(room.getName()));
    }

    private void get(RoutingContext routingContext) {
        final RoomsApi api = RoomsApi.get(routingContext);
        final Future<ListOfChatRooms> result = Future.future();
        result.setHandler(responseHandler(routingContext, this::cleanChatRooms));

        try {
            final Optional<Integer> skip = Optional.ofNullable(routingContext.queryParams().get("skip")).map(Integer::parseInt);
            final Optional<Integer> limit = Optional.ofNullable(routingContext.queryParams().get("limit")).map(Integer::parseInt);
            final Optional<String> filter = Optional.ofNullable(routingContext.queryParams().get("filter"));

            api.readAllChatRooms(skip.orElse(0), limit.orElse(10), filter.orElse(""), result.completer());
        } catch(HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
	}

    private ListOfChatRooms cleanChatRooms(ListOfChatRooms x) {
        return new ListOfChatRooms(
                x.stream().map(r -> new ChatRoom().setLink(r.getLink()))
        );
    }

}
