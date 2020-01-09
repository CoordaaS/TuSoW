package it.unibo.coordination.linda.text

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicSerializer
import it.unibo.presentation.MIMETypes

internal class RegexTemplateSerializer(mimeType: MIMETypes, mapper: ObjectMapper) : DynamicSerializer<RegexTemplate>(mimeType, mapper) {
    override fun toDynamicObject(`object`: RegexTemplate): Any {
        return mapOf("template" to `object`.template.pattern())
    }
}