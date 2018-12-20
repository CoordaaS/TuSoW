package it.unibo.coordination.tusow.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.presentation.ChatMessage;
import it.unibo.coordination.tusow.presentation.ListOfMessages;

public interface MessagesApi extends Api {

    void createChatRoomMessage(String chatRoomName, ChatMessage newMessage, Handler<AsyncResult<ChatMessage>> handler);
    

    void readChatRoomMessages(String chatRoomName, Integer skip, Integer limit, String filter, Handler<AsyncResult<ListOfMessages>> handler);


    static MessagesApi get(RoutingContext context) {
        return null;
    }
}
