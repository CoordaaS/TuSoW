package it.unibo.coordination.tusow.api

import alice.tuprolog.Term
import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicMatch.Companion.wrap
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.tusow.linda.TupleSpaces

internal class LogicTupleSpaceApiImpl(routingContext: RoutingContext) : AbstractTupleSpaceApi<LogicTuple, LogicTemplate, String, Term, LogicMatch, LogicSpace>(routingContext), LogicTupleSpaceApi {

    override fun getTupleSpaceByName(name: String): LogicSpace {
        return TupleSpaces.getLogicSpace(name)
    }

    override fun ensureCorrectTypeForMatch(match: Match<LogicTuple, LogicTemplate, String, Term>): LogicMatch {
        return wrap(match)
    }
}