package it.unibo.presentation

import java.io.Writer

interface Serializer<T> {
    val supportedMIMEType: MIMETypes

    fun toDynamicObject(`object`: T): Any

    @JvmDefault
    fun toDynamicObject(objects: Collection<T>): List<Any> {
        return objects.asSequence()
                .map { `object`: T -> this.toDynamicObject(`object`) }
                .toList()
    }

    fun toString(`object`: T): String

    fun toString(objects: Collection<T>): String

    fun write(`object`: T, writer: Writer)

    fun write(objects: Collection<T>, writer: Writer)
}