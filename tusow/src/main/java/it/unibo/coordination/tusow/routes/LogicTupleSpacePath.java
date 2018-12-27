package it.unibo.coordination.tusow.routes;

import alice.tuprolog.Term;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.tusow.api.LogicTupleSpaceApi;
import it.unibo.coordination.tusow.api.TupleSpaceApi;
import it.unibo.coordination.tusow.presentation.MIMETypes;
import it.unibo.coordination.tusow.presentation.Marshaller;
import it.unibo.coordination.tusow.presentation.Presentation;
import it.unibo.coordination.tusow.presentation.Unmarshaller;

public class LogicTupleSpacePath extends AbstractTupleSpacePath<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    public LogicTupleSpacePath() {
        super("logic");
    }

    @Override
    protected TupleSpaceApi<LogicTuple, LogicTemplate, String, Term, LogicMatch> getTupleSpaceApi(RoutingContext routingContext) {
        return LogicTupleSpaceApi.get(routingContext);
    }

    @Override
    protected Marshaller<LogicTuple> getTuplesMarshaller(MIMETypes mimeType) {
        return Presentation.getMarshaller(LogicTuple.class, mimeType);
    }

    @Override
    protected Marshaller<LogicTemplate> getTemplatesMarshaller(MIMETypes mimeType) {
        return Presentation.getMarshaller(LogicTemplate.class, mimeType);
    }

    @Override
    protected Marshaller<LogicMatch> getMatchMarshaller(MIMETypes mimeType) {
        return Presentation.getMarshaller(LogicMatch.class, mimeType);
    }

    @Override
    protected Unmarshaller<LogicTuple> getTuplesUnmarshaller(MIMETypes mimeType) {
        return Presentation.getUnmarshaller(LogicTuple.class, mimeType);
    }

    @Override
    protected Unmarshaller<LogicTemplate> getTemplatesUnmarshaller(MIMETypes mimeType) {
        return Presentation.getUnmarshaller(LogicTemplate.class, mimeType);
    }

    @Override
    protected Unmarshaller<LogicMatch> getMatchUnmarshaller(MIMETypes mimeType) {
        return Presentation.getUnmarshaller(LogicMatch.class, mimeType);
    }


}