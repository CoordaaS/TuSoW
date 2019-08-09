package it.unibo.coordination.tusow.api;

import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.string.RegexTemplate;
import it.unibo.coordination.linda.string.RegularMatch;
import it.unibo.coordination.linda.string.StringSpace;
import it.unibo.coordination.linda.string.StringTuple;
import it.unibo.coordination.tusow.linda.TupleSpaces;

class TextualTupleSpaceApiImpl extends AbstractTupleSpaceApiTupleSpaceApi<StringTuple, RegexTemplate, Object, String, RegularMatch, StringSpace>
        implements TextualTupleSpaceApi {

    TextualTupleSpaceApiImpl(RoutingContext routingContext) {
        super(routingContext);
    }

    @Override
    protected StringSpace getTupleSpaceByName(String name) {
        return TupleSpaces.getTextualSpace(name);
    }

    @Override
    protected RegularMatch ensureCorrectTypeForMatch(Match<StringTuple, RegexTemplate, Object, String> match) {
        return RegularMatch.wrap(match);
    }


}
