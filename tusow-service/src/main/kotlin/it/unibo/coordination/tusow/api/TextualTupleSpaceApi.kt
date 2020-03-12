package it.unibo.coordination.tusow.api

import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.linda.text.RegexTemplate
import it.unibo.coordination.linda.text.RegularMatch
import it.unibo.coordination.linda.text.StringTuple

interface TextualTupleSpaceApi : TupleSpaceApi<StringTuple, RegexTemplate, Any, String, RegularMatch> {
    companion object {
        @JvmStatic
        operator fun get(context: RoutingContext): TextualTupleSpaceApi {
            return TextualTupleSpaceApiImpl(context)
        }
    }
}