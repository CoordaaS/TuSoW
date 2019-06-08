package it.unibo.coordination.tusow.routes;

import alice.tuprolog.Term;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.linda.presentation.Deserializer;
import it.unibo.coordination.linda.presentation.MIMETypes;
import it.unibo.coordination.linda.presentation.Presentation;
import it.unibo.coordination.linda.presentation.Serializer;
import it.unibo.coordination.tusow.api.LogicTupleSpaceApi;
import it.unibo.coordination.tusow.api.TupleSpaceApi;

public class LogicTupleSpacePath extends AbstractTupleSpacePath<LogicTuple, LogicTemplate, String, Term, LogicMatch> {

    public LogicTupleSpacePath() {
        super("logic");
    }

    @Override
    protected TupleSpaceApi<LogicTuple, LogicTemplate, String, Term, LogicMatch> getTupleSpaceApi(RoutingContext routingContext) {
        return LogicTupleSpaceApi.get(routingContext);
    }

    @Override
    protected <N extends Number> LogicTuple numberToTuple(N x) {
        return LogicTuple.of(x.toString());
    }

    @Override
    protected Serializer<LogicTuple> getTuplesMarshaller(MIMETypes mimeType) {
        return Presentation.getSerializer(LogicTuple.class, mimeType);
    }

    @Override
    protected Serializer<LogicTemplate> getTemplatesMarshaller(MIMETypes mimeType) {
        return Presentation.getSerializer(LogicTemplate.class, mimeType);
    }

    @Override
    protected Serializer<LogicMatch> getMatchMarshaller(MIMETypes mimeType) {
        return Presentation.getSerializer(LogicMatch.class, mimeType);
    }

    @Override
    protected Deserializer<LogicTuple> getTuplesUnmarshaller(MIMETypes mimeType) {
        return Presentation.getDeserializer(LogicTuple.class, mimeType);
    }

    @Override
    protected Deserializer<LogicTemplate> getTemplatesUnmarshaller(MIMETypes mimeType) {
        return Presentation.getDeserializer(LogicTemplate.class, mimeType);
    }

    @Override
    protected Deserializer<LogicMatch> getMatchUnmarshaller(MIMETypes mimeType) {
        return Presentation.getDeserializer(LogicMatch.class, mimeType);
    }


}