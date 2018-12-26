package it.unibo.coordination.tusow.routes;

import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.api.LogicTupleSpaceApi;
import it.unibo.coordination.tusow.api.TupleSpaceApi;

import java.io.IOException;

public class LogicTupleSpacePath extends AbstractTupleSpacePath<LogicTupleRepresentation, LogicTemplateRepresentation> {

    public LogicTupleSpacePath() {
        super("logic");
    }

    @Override
    protected TupleSpaceApi<LogicTupleRepresentation, LogicTemplateRepresentation> getTupleSpaceApi(RoutingContext routingContext) {
        return LogicTupleSpaceApi.get(routingContext);
    }

    @Override
    protected ListRepresentation<LogicTupleRepresentation> parseTuples(String mimeType, String payload) throws IOException {
        return ListOfLogicTupleRepresentation.parse(mimeType, payload);
    }

    @Override
    protected LogicTemplateRepresentation parseTemplate(String mimeType, String payload) throws IOException {
        return LogicTemplateRepresentation.parse(mimeType, payload);
    }
}