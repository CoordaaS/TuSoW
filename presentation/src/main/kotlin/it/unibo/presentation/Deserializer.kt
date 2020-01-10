package it.unibo.presentation

import java.io.Reader

interface Deserializer<T> {
    val supportedType: TypeToken<T>

    val supportedMIMEType: MIMETypes

    fun fromDynamicObject(dynamicObject: Any): T

    @JvmDefault
    fun listFromDynamicObject(dynamicObject: Any): List<T> {
        return when(dynamicObject) {
            is Map<*, *> -> listOf(fromDynamicObject(dynamicObject))
            is List<*> -> dynamicObject.map { fromDynamicObject(it!!) }
            else -> throw IllegalArgumentException()
        }
    }

    fun fromString(string: String): T

    fun listFromString(string: String): List<T>

    fun read(reader: Reader): T

    fun readList(reader: Reader): List<T>

}