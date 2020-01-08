package it.unibo.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.Reader

abstract class DynamicDeserializer<T>(clazz: Class<T>, mimeType: MIMETypes, mapper: ObjectMapper) : SimpleDeserializer<T>(clazz, mimeType, mapper) {

    abstract override fun fromDynamicObject(dynamicObject: Any): T

    override fun read(reader: Reader): T {
        return fromDynamicObject(readImpl(reader, Any::class.java))
    }

    override fun readList(reader: Reader): List<T> {
        return listFromDynamicObject(readImpl(reader, Any::class.java))
    }
}