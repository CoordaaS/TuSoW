package it.unibo.coordination.tusow.routes

import alice.tuprolog.Term
import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.linda.logic.LogicTuple.Companion.of
import it.unibo.coordination.tusow.api.LogicTupleSpaceApi
import it.unibo.coordination.tusow.api.TupleSpaceApi
import it.unibo.presentation.Deserializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.Presentation
import it.unibo.presentation.Serializer

class LogicTupleSpacePath : AbstractTupleSpacePath<LogicTuple, LogicTemplate, String, Term, LogicMatch>("logic") {
    override fun getTupleSpaceApi(routingContext: RoutingContext): TupleSpaceApi<LogicTuple, LogicTemplate, String, Term, LogicMatch> {
        return LogicTupleSpaceApi[routingContext]
    }

    override val presentation: Presentation
        protected get() = it.unibo.coordination.linda.logic.Presentation

    override fun <N : Number> numberToTuple(x: N): LogicTuple {
        return of(x.toString())
    }

    override fun getTuplesMarshaller(mimeType: MIMETypes): Serializer<LogicTuple> {
        return presentation.serializerOf(LogicTuple::class.java, mimeType)
    }

    override fun getTemplatesMarshaller(mimeType: MIMETypes): Serializer<LogicTemplate> {
        return presentation.serializerOf(LogicTemplate::class.java, mimeType)
    }

    override fun getMatchMarshaller(mimeType: MIMETypes): Serializer<LogicMatch> {
        return presentation.serializerOf(LogicMatch::class.java, mimeType)
    }

    override fun getTuplesUnmarshaller(mimeType: MIMETypes): Deserializer<LogicTuple> {
        return presentation.deserializerOf(LogicTuple::class.java, mimeType)
    }

    override fun getTemplatesUnmarshaller(mimeType: MIMETypes): Deserializer<LogicTemplate> {
        return presentation.deserializerOf(LogicTemplate::class.java, mimeType)
    }

    override fun getMatchUnmarshaller(mimeType: MIMETypes): Deserializer<LogicMatch> {
        return presentation.deserializerOf(LogicMatch::class.java, mimeType)
    }
}