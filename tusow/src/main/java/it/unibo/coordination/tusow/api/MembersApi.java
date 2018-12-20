package it.unibo.coordination.tusow.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.presentation.Link;
import it.unibo.coordination.tusow.presentation.ListOfUsers;
import it.unibo.coordination.tusow.presentation.User;

public interface MembersApi extends Api {

    void createChatRoomMember(String chatRoomName, User member, Handler<AsyncResult<Link>> handler);
    

    void deleteChatRoomMember(String chatRoomName, String memberId, Handler<AsyncResult<Void>> handler);
    

    void readChatRoomMembers(String chatRoomName, Integer skip, Integer limit, String filter, Handler<AsyncResult<ListOfUsers>> handler);

    static MembersApi get(RoutingContext context) {
        return null;
    }
}
