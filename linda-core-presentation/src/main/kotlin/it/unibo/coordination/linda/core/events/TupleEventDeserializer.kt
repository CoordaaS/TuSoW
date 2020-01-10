package it.unibo.coordination.linda.core.events

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.coordination.linda.core.Presentation.fromDynamicObject
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.TypeToken
import it.unibo.presentation.toTypeToken

@Suppress("UNCHECKED_CAST")
class TupleEventDeserializer<T : Tuple<T>, TT : Template<T>>(val tupleType: Class<T>, val templateType: Class<TT>, mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicDeserializer<TupleEvent<T, TT>>(
        TupleEvent::class.java.toTypeToken(tupleType, templateType) as TypeToken<TupleEvent<T, TT>>,
        mimeType,
        mapper
) {

    constructor(typeToken: TypeToken<TupleEvent<T, TT>>, mimeType: MIMETypes, mapper: ObjectMapper) :
            this(typeToken.genericTypes[0] as Class<T>, typeToken.genericTypes[1] as Class<TT>, mimeType, mapper)

    override fun fromDynamicObject(dynamicObject: Any): TupleEvent<T, TT> {
        if (dynamicObject is Map<*, *>) {
            val tupleSpaceName = dynamicObject["tupleSpaceName"] as String
            val isBefore = dynamicObject["isBefore"] as String
            val effect = dynamicObject["effect"] as String
            val tuple = dynamicObject["tuple"]
            val template = dynamicObject["template"]

            return TupleEvent.of(
                    tupleSpaceName,
                    isBefore.toBoolean(),
                    TupleEvent.Effect.valueOf(effect),
                    tuple.fromDynamicObject(tupleType, supportedMIMEType),
                    template.fromDynamicObject(templateType, supportedMIMEType)
            )
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }
}