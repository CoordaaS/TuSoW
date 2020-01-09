package it.unibo.coordination.tusow.routes

class TupleSpacesPath(version: String, root: String) : Path("/tusow/v$version/$root") {

    override fun setupRoutes() {
        append(LogicTupleSpacePath())
        append(TextualTupleSpacePath())
    }

}