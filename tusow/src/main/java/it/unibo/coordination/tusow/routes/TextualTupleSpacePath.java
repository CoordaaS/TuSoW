package it.unibo.coordination.tusow.routes;

import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.text.RegexTemplate;
import it.unibo.coordination.linda.text.RegularMatch;
import it.unibo.coordination.linda.text.StringTuple;
import it.unibo.coordination.tusow.api.TextualTupleSpaceApi;
import it.unibo.coordination.tusow.api.TupleSpaceApi;
import it.unibo.presentation.Deserializer;
import it.unibo.presentation.MIMETypes;
import it.unibo.presentation.Presentation;
import it.unibo.presentation.Serializer;

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
    protected Presentation getPresentation() {
        return it.unibo.coordination.linda.text.Presentation.INSTANCE;
    }

    @Override
    protected Serializer<StringTuple> getTuplesMarshaller(MIMETypes mimeType) {
        return getPresentation().serializerOf(StringTuple.class, mimeType);
    }

    @Override
    protected Serializer<RegexTemplate> getTemplatesMarshaller(MIMETypes mimeType) {
        return getPresentation().serializerOf(RegexTemplate.class, mimeType);
    }

    @Override
    protected Serializer<RegularMatch> getMatchMarshaller(MIMETypes mimeType) {
        return getPresentation().serializerOf(RegularMatch.class, mimeType);
    }

    @Override
    protected Deserializer<StringTuple> getTuplesUnmarshaller(MIMETypes mimeType) {
        return getPresentation().deserializerOf(StringTuple.class, mimeType);
    }

    @Override
    protected Deserializer<RegexTemplate> getTemplatesUnmarshaller(MIMETypes mimeType) {
        return getPresentation().deserializerOf(RegexTemplate.class, mimeType);
    }

    @Override
    protected Deserializer<RegularMatch> getMatchUnmarshaller(MIMETypes mimeType) {
        return getPresentation().deserializerOf(RegularMatch.class, mimeType);
    }
}