package it.unibo.coordination.linda.core.events

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.coordination.linda.core.Presentation.toDynamicObject
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.presentation.DynamicSerializer
import it.unibo.presentation.MIMETypes

class TupleEventSerializer<T : Tuple<T>, TT : Template<T>>(mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicSerializer<TupleEvent<T, TT>>(mimeType, mapper) {

    override fun toDynamicObject(`object`: TupleEvent<T, TT>): Any {
        return with(`object`) {
            mapOf(
                    "tupleSpaceName" to tupleSpaceName,
                    "isBefore" to isBefore.toString(),
                    "effect" to effect.toString(),
                    "tuple" to tuple.toDynamicObject(supportedMIMEType),
                    "template" to template.toDynamicObject(supportedMIMEType)
            )
        }
    }
}