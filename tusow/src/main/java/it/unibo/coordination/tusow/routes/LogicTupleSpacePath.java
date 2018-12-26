package it.unibo.coordination.tusow.routes;

import alice.tuprolog.Term;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.tusow.api.LogicTupleSpaceApi;
import it.unibo.coordination.tusow.api.TupleSpaceApi;

import java.io.IOException;
import java.util.List;

public class LogicTupleSpacePath extends AbstractTupleSpacePath<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    public LogicTupleSpacePath() {
        super("logic");
    }

    @Override
    protected TupleSpaceApi<LogicTuple, LogicTemplate, String, Term, LogicMatch> getTupleSpaceApi(RoutingContext routingContext) {
        return LogicTupleSpaceApi.get(routingContext);
    }

    @Override
    protected List<LogicTuple> parseTuples(String mimeType, String payload) throws IOException {
        return null;
    }

    @Override
    protected LogicTemplate parseTemplate(String mimeType, String payload) throws IOException {
        return null;
    }

//    @Override
//    protected TupleSpaceApi<LogicTupleRepresentation, LogicTemplateRepresentation> getTupleSpaceApi(RoutingContext routingContext) {
//        return LogicTupleSpaceApi.get(routingContext);
//    }
//
//    @Override
//    protected ListRepresentation<LogicTupleRepresentation> parseTuples(String mimeType, String payload) throws IOException {
//        return ListOfLogicTupleRepresentation.parse(mimeType, payload);
//    }
//
//    @Override
//    protected LogicTemplateRepresentation parseTemplate(String mimeType, String payload) throws IOException {
//        return LogicTemplateRepresentation.parse(mimeType, payload);
//    }
}