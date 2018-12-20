package it.unibo.coordination.tusow.routes;

public class WebChatPath extends Path {

	public WebChatPath(String version) {
		super("/web-chat/v" + version);
	}

    @Override
    protected void setupRoutes() {
        append(new UsersPath());
        append(new RoomsPath());
    }
}
