package it.unibo.coordination.tusow.api

import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.tuprolog.core.Term

interface LogicTupleSpaceApi : TupleSpaceApi<LogicTuple, LogicTemplate, String, Term, LogicMatch> {
    companion object {
        @JvmStatic
        operator fun get(context: RoutingContext): LogicTupleSpaceApi {
            return LogicTupleSpaceApiImpl(context)
        }
    }
}