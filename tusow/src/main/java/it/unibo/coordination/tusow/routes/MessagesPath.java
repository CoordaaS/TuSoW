package it.unibo.coordination.tusow.routes;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.api.MessagesApi;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.HttpError;
import it.unibo.coordination.tusow.presentation.ChatMessage;
import it.unibo.coordination.tusow.presentation.ListOfMessages;
import it.unibo.coordination.tusow.presentation.User;

import java.io.IOException;
import java.util.Optional;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public class MessagesPath extends Path {


    public MessagesPath() {
        super("/messages");
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
    }


    private void post(RoutingContext routingContext) {
        final MessagesApi api = MessagesApi.get(routingContext);
        final Future<ChatMessage> result = Future.future();
        result.setHandler(responseHandler(routingContext, this::cleanMessage));

        try {
            final String chatRoomName = routingContext.pathParam("chatRoomName");
            final ChatMessage message = ChatMessage.parse(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());
            validateMessageForPost(message);

            api.createChatRoomMessage(chatRoomName, message, result.completer());
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private void validateMessageForPost(ChatMessage member) {
        requireAllAreNull(member.getIndex(), member.getTimestamp(), member.getChatRoom(), member.getSender());
        requireNoneIsNull(member.getContent());
    }

    private void get(RoutingContext routingContext) {
        final MessagesApi api = MessagesApi.get(routingContext);
        final Future<ListOfMessages> result = Future.future();
        result.setHandler(responseHandler(routingContext, this::cleanMessages));

        try {
            final String chatRoomName = routingContext.pathParam("chatRoomName");
            final Optional<Integer> skip = Optional.ofNullable(routingContext.queryParams().get("skip")).map(Integer::parseInt);
            final Optional<Integer> limit = Optional.ofNullable(routingContext.queryParams().get("limit")).map(Integer::parseInt);
            final Optional<String> filter = Optional.ofNullable(routingContext.queryParams().get("filter"));

            api.readChatRoomMessages(chatRoomName, skip.orElse(0), limit.orElse(10), filter.orElse(""), result.completer());
        } catch (HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private ListOfMessages cleanMessages(ListOfMessages messages) {
        return new ListOfMessages(
                messages.stream().map(this::cleanMessage)
        );
    }

    private ChatMessage cleanMessage(ChatMessage message) {
        final User sender = new User();
        if (message.getSender() != null) {
            sender.setLink(message.getSender().getLink());
        }
        return new ChatMessage(message).setSender(sender);
    }

}
