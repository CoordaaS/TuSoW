package it.unibo.coordination.linda.text

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicSerializer
import it.unibo.presentation.MIMETypes
import java.util.*

internal class RegularMatchSerializer(mimeType: MIMETypes, mapper: ObjectMapper) : DynamicSerializer<RegularMatch>(mimeType, mapper) {
    override fun toDynamicObject(`object`: RegularMatch): Any {
        val matchMap: MutableMap<String, Any?> = HashMap()
        matchMap["tuple"] = `object`.tuple.map<String?> { obj: StringTuple -> obj.value }.orElse(null)
        matchMap["template"] = `object`.template.template.pattern()
        matchMap["match"] = `object`.isMatching
        matchMap["map"] = `object`.toMap()
        return matchMap
    }
}