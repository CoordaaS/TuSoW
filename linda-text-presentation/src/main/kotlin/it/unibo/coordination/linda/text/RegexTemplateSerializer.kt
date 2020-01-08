package it.unibo.coordination.linda.text

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.coordination.utils.CollectionUtils
import it.unibo.presentation.DynamicSerializer
import it.unibo.presentation.MIMETypes

internal class RegexTemplateSerializer(mimeType: MIMETypes, mapper: ObjectMapper) : DynamicSerializer<RegexTemplate>(mimeType, mapper) {
    override fun toDynamicObject(`object`: RegexTemplate): Any {
        return CollectionUtils.mapOf("template", `object`.template.pattern())
    }
}