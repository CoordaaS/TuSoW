package it.unibo.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.Writer

internal abstract class DynamicSerializer<T>(mimeType: MIMETypes, mapper: ObjectMapper)
    : SimpleSerializer<T>(mimeType, mapper) {

    abstract override fun toDynamicObject(`object`: T): Any

    override fun write(`object`: T, writer: Writer) {
        writeImpl(toDynamicObject(`object`), writer)
    }

    override fun write(objects: Collection<T>, writer: Writer) {
        writeImpl(toDynamicObject(objects), writer)
    }
}