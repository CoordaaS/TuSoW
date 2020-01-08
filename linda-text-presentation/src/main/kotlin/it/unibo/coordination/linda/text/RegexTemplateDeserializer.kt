package it.unibo.coordination.linda.text

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes

internal class RegexTemplateDeserializer(mimeType: MIMETypes, mapper: ObjectMapper) : DynamicDeserializer<RegexTemplate>(RegexTemplate::class.java, mimeType, mapper) {
    override fun fromDynamicObject(dynamicObject: Any): RegexTemplate {
        if (dynamicObject is Map<*, *>) {
            val map = dynamicObject as Map<String, *>
            if (map.containsKey("template")) {
                val tuple = map["template"]
                if (tuple is String) {
                    return RegexTemplate.of(tuple)
                }
            }
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }
}