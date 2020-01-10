package it.unibo.presentation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.io.Reader
import java.io.StringReader

open class SimpleDeserializer<T>(override val supportedType: TypeToken<T>, override val supportedMIMEType: MIMETypes, protected val mapper: ObjectMapper) : Deserializer<T> {

    constructor(supportedType: Class<T>, supportedMIMEType: MIMETypes, mapper: ObjectMapper)
        : this(supportedType.toTypeToken(), supportedMIMEType, mapper)

    override fun fromDynamicObject(dynamicObject: Any): T {
        throw UnsupportedOperationException()
    }

    override fun fromString(string: String): T {
        return read(StringReader(string))
    }

    override fun listFromString(string: String): List<T> {
        return readList(StringReader(string))
    }

    override fun read(reader: Reader): T {
        return readImpl(reader, supportedType.type)
    }

    protected fun <X> readImpl(reader: Reader, clazz: Class<X>): X {
        return try {
            mapper.readValue(reader, clazz)
        } catch (e: IOException) {
            throw IllegalArgumentException("Cannot read $supportedMIMEType", e)
        }
    }

    protected fun <X> readImpl(reader: Reader, clazz: TypeReference<X>): X {
        return try {
            mapper.readValue(reader, clazz)
        } catch (e: IOException) {
            throw IllegalArgumentException("Cannot read $supportedMIMEType", e)
        }
    }

    override fun readList(reader: Reader): List<T> {
        return readImpl(reader, object : TypeReference<List<T>>() {})
    }

}