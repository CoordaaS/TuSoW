package it.unibo.coordination.linda.core

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicSerializer
import it.unibo.presentation.MIMETypes

class PendingRequestSerializer<T : Tuple<T>, TT : Template<T>>(mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicSerializer<PendingRequest<T, TT>>(mimeType, mapper) {

    override fun toDynamicObject(`object`: PendingRequest<T, TT>): Any {
        return with(`object`) {
            val templateObj = Presentation.serializerOf(template.javaClass, supportedMIMEType).toDynamicObject(template)
            mapOf(
                    "id" to id,
                    "requestType" to requestType.toString(),
                    "template" to templateObj
            )
        }
    }
}