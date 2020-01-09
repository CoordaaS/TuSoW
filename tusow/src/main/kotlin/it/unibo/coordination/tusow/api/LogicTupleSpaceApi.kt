package it.unibo.coordination.tusow.api

import alice.tuprolog.Term
import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple

interface LogicTupleSpaceApi : TupleSpaceApi<LogicTuple, LogicTemplate, String, Term, LogicMatch> {
    companion object {
        @JvmStatic
        operator fun get(context: RoutingContext): LogicTupleSpaceApi {
            return LogicTupleSpaceApiImpl(context)
        }
    }
}