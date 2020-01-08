package it.unibo.coordination.linda.text

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes

internal class RegularMatchDeserializer(mimeType: MIMETypes, mapper: ObjectMapper) : DynamicDeserializer<RegularMatch>(RegularMatch::class.java, mimeType, mapper) {
    override fun fromDynamicObject(dynamicObject: Any): RegularMatch {
        if (dynamicObject is Map<*, *>) {
            val dynamicMap = dynamicObject as Map<String, *>
            when (val templateStr = dynamicMap["template"] ) {
                is String -> {
                    val template = RegexTemplate.of(templateStr)
                    var tupleObject: Any
                    if (dynamicMap["tuple"].also { tupleObject = it!! } is String) {
                        val tuple = StringTuple.of(tupleObject as String)
                        return template.matchWith(tuple)
                    }
                    return RegularMatch.failed(template)
                }
            }
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }
}