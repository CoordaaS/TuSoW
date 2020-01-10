package it.unibo.coordination.linda.core.events

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.coordination.linda.core.PendingRequest
import it.unibo.coordination.linda.core.Presentation.fromDynamicObject
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.TypeToken
import it.unibo.presentation.toTypeToken

@Suppress("UNCHECKED_CAST")
class PendingRequestEventDeserializer<T : Tuple<T>, TT : Template<T>>(val tupleType: Class<T>, val templateType: Class<TT>, mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicDeserializer<PendingRequestEvent<T, TT>>(
        PendingRequestEvent::class.java.toTypeToken(tupleType, templateType) as TypeToken<PendingRequestEvent<T, TT>>,
        mimeType,
        mapper
) {

    constructor(typeToken: TypeToken<PendingRequestEvent<T, TT>>, mimeType: MIMETypes, mapper: ObjectMapper) :
            this(typeToken.genericTypes[0] as Class<T>, typeToken.genericTypes[1] as Class<TT>, mimeType, mapper)

    override fun fromDynamicObject(dynamicObject: Any): PendingRequestEvent<T, TT> {
        if (dynamicObject is Map<*, *>) {
            val tupleSpaceName = dynamicObject["tupleSpaceName"] as String
            val effect = dynamicObject["effect"] as String
            val pendingRequestObject = dynamicObject["pendingRequest"]

            return PendingRequestEvent.of(
                    tupleSpaceName,
                    PendingRequestEvent.Effect.valueOf(effect),
                    pendingRequestObject.fromDynamicObject(
                            PendingRequest::class.java.toTypeToken(tupleType, templateType) as TypeToken<PendingRequest<T, TT>>,
                            supportedMIMEType
                    )!!
            )
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }
}