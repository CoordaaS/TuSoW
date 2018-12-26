package it.unibo.coordination.tusow.api;

import alice.tuprolog.Term;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;

public interface LogicTupleSpaceApi extends TupleSpaceApi<LogicTuple, LogicTemplate, String, Term, LogicMatch> {
    static LogicTupleSpaceApi get(RoutingContext context) {
        return new LogicTupleSpaceApiImpl(context);
    }
}
