package it.unibo.coordination.tusow.api;

import alice.tuprolog.Term;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicSpace;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.tusow.linda.TupleSpaces;

class LogicTupleSpaceApiImpl extends AbstractTupleSpaceApi<LogicTuple, LogicTemplate, String, Term, LogicMatch, LogicSpace>
        implements LogicTupleSpaceApi {

    LogicTupleSpaceApiImpl(RoutingContext routingContext) {
        super(routingContext);
    }

    @Override
    protected LogicSpace getTupleSpaceByName(String name) {
        return TupleSpaces.getLogicSpace(name);
    }

    @Override
    protected LogicMatch ensureCorrectTypeForMatch(Match<LogicTuple, LogicTemplate, String, Term> match) {
        return LogicMatch.wrap(match);
    }

}
