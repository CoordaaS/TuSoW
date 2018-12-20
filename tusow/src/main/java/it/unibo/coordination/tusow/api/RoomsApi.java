package it.unibo.coordination.tusow.api;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.presentation.ChatRoom;
import it.unibo.coordination.tusow.presentation.Link;
import it.unibo.coordination.tusow.presentation.ListOfChatRooms;


public interface RoomsApi extends Api {

    void createChatRoom(ChatRoom chatRoom, Handler<AsyncResult<Link>> handler);
    

    void deleteChatRoom(String chatRoomName, Handler<AsyncResult<Void>> handler);
    

    void readAllChatRooms(Integer skip, Integer limit, String filter, Handler<AsyncResult<ListOfChatRooms>> handler);
    

    void readChatRoom(String chatRoomName, Integer limitMessages, Integer limitMembers, Handler<AsyncResult<ChatRoom>> handler);

    static RoomsApi get(RoutingContext context) {
        return null;
    }
}
