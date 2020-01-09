package it.unibo.coordination.linda.text

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes

internal class StringTupleDeserializer(mimeType: MIMETypes, mapper: ObjectMapper) : DynamicDeserializer<StringTuple>(StringTuple::class.java, mimeType, mapper) {
    override fun fromDynamicObject(dynamicObject: Any): StringTuple {
        if (dynamicObject is Map<*, *>) {
            if (dynamicObject.containsKey("tuple")) {
                val tuple = dynamicObject["tuple"]
                if (tuple is String) {
                    return StringTuple.of(tuple)
                }
            }
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }
}