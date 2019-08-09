package it.unibo.coordination.tusow.api;

import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringTuple;

public interface TextualTupleSpaceApi extends TupleSpaceApi<StringTuple, RegexTemplate, Object, String, RegularMatch> {
    static TextualTupleSpaceApi get(RoutingContext context) {
        return new TextualTupleSpaceApiImpl(context);
    }
}
