package it.unibo.coordination.linda.logic

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes

internal class LogicMatchDeserializer(mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicDeserializer<LogicMatch>(LogicMatch::class.java, mimeType, mapper) {

    override fun fromDynamicObject(dynamicObject: Any): LogicMatch {
        var dynamicObject: Any? = dynamicObject
        if (dynamicObject is List<*>) {
            if (dynamicObject.size == 1) {
                dynamicObject = dynamicObject[0]
            }
        }
        if (dynamicObject is Map<*, *>) {

            val dynamicMap = dynamicObject as Map<String, *>
            if (dynamicMap.containsKey("template")) {
                val template = Presentation.deserializerOf(LogicTemplate::class.java, supportedMIMEType)
                        .fromDynamicObject(dynamicMap["template"]!!)
                var tupleObject: Any
                if (dynamicMap["tuple"].also { tupleObject = it!! } != null) {
                    val tuple = Presentation.deserializerOf(LogicTuple::class.java, supportedMIMEType)
                            .fromDynamicObject(tupleObject)
                    return template.matchWith(tuple)
                }
                return LogicMatch.failed(template)
            }
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }
}