package it.unibo.coordination.linda.text

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes

internal class RegularMatchDeserializer(mimeType: MIMETypes, mapper: ObjectMapper) : DynamicDeserializer<RegularMatch>(RegularMatch::class.java, mimeType, mapper) {
    override fun fromDynamicObject(dynamicObject: Any): RegularMatch {
        if (dynamicObject is Map<*, *>) {
            when (val templateStr = dynamicObject["template"] ) {
                is String -> {
                    val template = RegexTemplate.of(templateStr)
                    when(val tupleObject: Any? = dynamicObject["tuple"]) {
                        is String -> {
                            val tuple = StringTuple.of(tupleObject)
                            return template.matchWith(tuple)
                        }
                    }
                    return RegularMatch.failed(template)
                }
            }
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }
}