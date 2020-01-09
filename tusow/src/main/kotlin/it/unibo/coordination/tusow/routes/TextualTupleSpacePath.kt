package it.unibo.coordination.tusow.routes

import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.linda.text.RegexTemplate
import it.unibo.coordination.linda.text.RegularMatch
import it.unibo.coordination.linda.text.StringTuple
import it.unibo.coordination.tusow.api.TextualTupleSpaceApi
import it.unibo.coordination.tusow.api.TupleSpaceApi
import it.unibo.presentation.Deserializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.Presentation
import it.unibo.presentation.Serializer

class TextualTupleSpacePath : AbstractTupleSpacePath<StringTuple, RegexTemplate, Any, String, RegularMatch>("textual") {
    override fun getTupleSpaceApi(routingContext: RoutingContext): TupleSpaceApi<StringTuple, RegexTemplate, Any, String, RegularMatch> {
        return TextualTupleSpaceApi[routingContext]
    }

    override fun <N : Number> numberToTuple(x: N): StringTuple {
        return StringTuple.of(x.toString())
    }

    override val presentation: Presentation
        protected get() = it.unibo.coordination.linda.text.Presentation

    override fun getTuplesMarshaller(mimeType: MIMETypes): Serializer<StringTuple> {
        return presentation.serializerOf(StringTuple::class.java, mimeType)
    }

    override fun getTemplatesMarshaller(mimeType: MIMETypes): Serializer<RegexTemplate> {
        return presentation.serializerOf(RegexTemplate::class.java, mimeType)
    }

    override fun getMatchMarshaller(mimeType: MIMETypes): Serializer<RegularMatch> {
        return presentation.serializerOf(RegularMatch::class.java, mimeType)
    }

    override fun getTuplesUnmarshaller(mimeType: MIMETypes): Deserializer<StringTuple> {
        return presentation.deserializerOf(StringTuple::class.java, mimeType)
    }

    override fun getTemplatesUnmarshaller(mimeType: MIMETypes): Deserializer<RegexTemplate> {
        return presentation.deserializerOf(RegexTemplate::class.java, mimeType)
    }

    override fun getMatchUnmarshaller(mimeType: MIMETypes): Deserializer<RegularMatch> {
        return presentation.deserializerOf(RegularMatch::class.java, mimeType)
    }
}