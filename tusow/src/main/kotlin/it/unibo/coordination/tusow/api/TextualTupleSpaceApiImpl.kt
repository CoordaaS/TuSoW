package it.unibo.coordination.tusow.api

import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.text.RegexTemplate
import it.unibo.coordination.linda.text.RegularMatch
import it.unibo.coordination.linda.text.StringTuple
import it.unibo.coordination.linda.text.TextualSpace
import it.unibo.coordination.tusow.linda.TupleSpaces

internal class TextualTupleSpaceApiImpl(routingContext: RoutingContext) : AbstractTupleSpaceApi<StringTuple, RegexTemplate, Any, String, RegularMatch, TextualSpace>(routingContext), TextualTupleSpaceApi {

    override fun getTupleSpaceByName(name: String): TextualSpace {
        return TupleSpaces.getTextualSpace(name)
    }

    override fun ensureCorrectTypeForMatch(match: Match<StringTuple, RegexTemplate, Any, String>): RegularMatch {
        return RegularMatch.wrap(match)
    }
}