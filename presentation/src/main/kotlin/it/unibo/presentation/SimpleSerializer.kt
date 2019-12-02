package it.unibo.presentation

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.io.StringWriter
import java.io.Writer

open class SimpleSerializer<T>(override val supportedMIMEType: MIMETypes, protected val mapper: ObjectMapper) : Serializer<T> {

    override fun toDynamicObject(`object`: T): Any {
        throw UnsupportedOperationException()
    }

    override fun toString(`object`: T): String {
        val sw = StringWriter()
        write(`object`, sw)
        return sw.toString()
    }

    override fun toString(objects: Collection<T>): String {
        val sw = StringWriter()
        write(objects, sw)
        return sw.toString()
    }

    override fun write(`object`: T, writer: Writer) {
        writeImpl(`object` as Any, writer)
    }

    protected fun writeImpl(`object`: Any, writer: Writer?) {
        try {
            mapper.writeValue(writer, `object`)
        } catch (e: JsonProcessingException) {
            throw IllegalArgumentException("Cannot convert " + `object` + " into " + supportedMIMEType, e)
        } catch (e: IOException) {
            throw IllegalStateException("Cannot convert " + `object` + " into " + supportedMIMEType, e)
        }
    }

    override fun write(objects: Collection<T>, writer: Writer) {
        writeImpl(objects, writer)
    }

}