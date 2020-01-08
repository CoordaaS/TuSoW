package it.unibo.coordination.linda.logic

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.prologx.PrologUtils
import it.unibo.presentation.DynamicSerializer
import it.unibo.presentation.MIMETypes
import java.util.*

internal class LogicMatchSerializer(mimeType: MIMETypes, mapper: ObjectMapper) : DynamicSerializer<LogicMatch>(mimeType, mapper) {

    override fun toDynamicObject(`object`: LogicMatch): Any {
        val matchMap: MutableMap<String, Any?> = HashMap()
        matchMap["tuple"] = `object`.tuple.map {
            Presentation.serializerOf(LogicTuple::class.java, supportedMIMEType).toDynamicObject(it)
        }.orElse(null)
        matchMap["template"] = Presentation.serializerOf(LogicTemplate::class.java, supportedMIMEType).toDynamicObject(`object`.template)
        matchMap["match"] = `object`.isMatching
        matchMap["map"] = `object`.toMap().mapValues { PrologUtils.termToDynamicObject(it.value) }
        return matchMap
    }
}