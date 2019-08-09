package it.unibo.coordination.tusow.routes;

import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.presentation.Deserializer;
import it.unibo.coordination.linda.presentation.MIMETypes;
import it.unibo.coordination.linda.presentation.Serializer;
import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringTuple;
import it.unibo.coordination.tusow.api.TextualTupleSpaceApi;
import it.unibo.coordination.tusow.api.TupleSpaceApi;

public class TextualTupleSpacePath extends AbstractTupleSpacePath<StringTuple, RegexTemplate, Object, String, RegularMatch> {

    public TextualTupleSpacePath() {
        super("textual");
    }

    @Override
    protected TupleSpaceApi<StringTuple, RegexTemplate, Object, String, RegularMatch> getTupleSpaceApi(RoutingContext routingContext) {
        return TextualTupleSpaceApi.get(routingContext);
    }

    @Override
    protected <N extends Number> StringTuple numberToTuple(N x) {
        return StringTuple.of(x.toString());
    }

    @Override
    protected Serializer<StringTuple> getTuplesMarshaller(MIMETypes mimeType) {
        return Serializer.of(StringTuple.class, mimeType);
    }

    @Override
    protected Serializer<RegexTemplate> getTemplatesMarshaller(MIMETypes mimeType) {
        return Serializer.of(RegexTemplate.class, mimeType);
    }

    @Override
    protected Serializer<RegularMatch> getMatchMarshaller(MIMETypes mimeType) {
        return Serializer.of(RegularMatch.class, mimeType);
    }

    @Override
    protected Deserializer<StringTuple> getTuplesUnmarshaller(MIMETypes mimeType) {
        return Deserializer.of(StringTuple.class, mimeType);
    }

    @Override
    protected Deserializer<RegexTemplate> getTemplatesUnmarshaller(MIMETypes mimeType) {
        return Deserializer.of(RegexTemplate.class, mimeType);
    }

    @Override
    protected Deserializer<RegularMatch> getMatchUnmarshaller(MIMETypes mimeType) {
        return Deserializer.of(RegularMatch.class, mimeType);
    }
}