package it.unibo.coordination.linda.core

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.TypeToken
import it.unibo.presentation.toTypeToken

@Suppress("UNCHECKED_CAST")
class PendingRequestDeserializer<T : Tuple<T>, TT : Template<T>>(val tupleType: Class<T>, val templateType: Class<TT>, mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicDeserializer<PendingRequest<T, TT>>(
        PendingRequest::class.java.toTypeToken(tupleType, templateType) as TypeToken<PendingRequest<T, TT>>,
        mimeType,
        mapper
) {

    constructor(typeToken: TypeToken<PendingRequest<T, TT>>, mimeType: MIMETypes, mapper: ObjectMapper) :
            this(typeToken.genericTypes[0] as Class<T>, typeToken.genericTypes[1] as Class<TT>, mimeType, mapper)

    override fun fromDynamicObject(dynamicObject: Any): PendingRequest<T, TT> {
        if (dynamicObject is Map<*, *>) {
            val idObj = dynamicObject["id"] as String
            val requestTypeObj = dynamicObject["requestType"] as String
            val templateObj = dynamicObject["template"]!!

            val template = Presentation.deserializerOf(templateType, supportedMIMEType).fromDynamicObject(templateObj)

            return PendingRequest.of(idObj, RequestTypes.valueOf(requestTypeObj), template)
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }
}