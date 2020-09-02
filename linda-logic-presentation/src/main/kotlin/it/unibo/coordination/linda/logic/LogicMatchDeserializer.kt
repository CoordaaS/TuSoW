package it.unibo.coordination.linda.logic

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes

internal class LogicMatchDeserializer(mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicDeserializer<LogicMatch>(LogicMatch::class.java, mimeType, mapper) {

    @Suppress("NAME_SHADOWING")
    override fun fromDynamicObject(dynamicObject: Any): LogicMatch {
        var dynamicObject: Any? = dynamicObject
        if (dynamicObject is List<*>) {
            if (dynamicObject.size == 1) {
                dynamicObject = dynamicObject[0]
            }
        }
        if (dynamicObject is Map<*, *>) {

            val dynamicMap = dynamicObject
            if (dynamicMap.containsKey("template")) {
                val templateObj = dynamicMap["template"]
                        ?: error("Missing template field in parsing a ${LogicMatch::class.simpleName}")
                val template = Presentation.deserializerOf(LogicTemplate::class.java, supportedMIMEType)
                        .fromDynamicObject(templateObj)
                val tupleObject = dynamicMap["tuple"]
                if (tupleObject != null) {
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