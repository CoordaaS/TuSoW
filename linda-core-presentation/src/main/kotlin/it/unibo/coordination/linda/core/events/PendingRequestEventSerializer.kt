package it.unibo.coordination.linda.core.events

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.coordination.linda.core.PendingRequest
import it.unibo.coordination.linda.core.Presentation.toDynamicObject
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.presentation.DynamicSerializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.TypeToken
import it.unibo.presentation.toTypeToken

@Suppress("UNCHECKED_CAST")
class PendingRequestEventSerializer<T : Tuple<T>, TT : Template<T>>(val tupleType: Class<T>, val templateType: Class<TT>, mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicSerializer<PendingRequestEvent<T, TT>>(mimeType, mapper) {

    constructor(typeToken: TypeToken<PendingRequestEvent<T, TT>>, mimeType: MIMETypes, mapper: ObjectMapper) :
            this(typeToken.genericTypes[0] as Class<T>, typeToken.genericTypes[1] as Class<TT>, mimeType, mapper)

    override fun toDynamicObject(`object`: PendingRequestEvent<T, TT>): Any {
        return with(`object`) {
            mapOf(
                    "tupleSpaceName" to tupleSpaceName,
                    "effect" to effect.toString(),
                    "pendingRequest" to this.pendingRequest.toDynamicObject(
                            PendingRequest::class.java.toTypeToken(tupleType, templateType) as TypeToken<PendingRequest<T, TT>>,
                            supportedMIMEType
                    )
            )
        }
    }

}