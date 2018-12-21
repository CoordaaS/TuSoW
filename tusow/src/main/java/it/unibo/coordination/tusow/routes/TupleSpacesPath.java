package it.unibo.coordination.tusow.routes;

public class TupleSpacesPath extends Path {

	public TupleSpacesPath(String version, String root) {
		super("/tusow/v" + version + "/" + root);
	}

    @Override
    protected void setupRoutes() {
        append(new LogicTupleSpacePath());
        append(new RoomsPath());
    }
}
