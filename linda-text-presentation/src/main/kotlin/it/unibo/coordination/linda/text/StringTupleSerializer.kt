package it.unibo.coordination.linda.text

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.presentation.DynamicSerializer
import it.unibo.presentation.MIMETypes

internal class StringTupleSerializer(mimeType: MIMETypes, mapper: ObjectMapper) : DynamicSerializer<StringTuple>(mimeType, mapper) {
    override fun toDynamicObject(`object`: StringTuple): Any {
        return mapOf("tuple" to `object`.value)
    }
}