package it.unibo.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

interface Presentation {
    fun <T> register(type: Class<T>, serializer: Serializer<T>)
    fun <T> register(type: Class<T>, deserializer: Deserializer<T>)

    @Suppress("UNCHECKED_CAST")
    fun <T> serializerOf(type: Class<T>, mimeType: MIMETypes): Serializer<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> deserializerOf(type: Class<T>, mimeType: MIMETypes): Deserializer<T>

    fun <T> registerSimpleSerializers(type: Class<T>)
    fun <T> registerSimpleSerializers(type: Class<T>, types: EnumSet<MIMETypes>)
    fun <T> registerDynamicSerializers(type: Class<T>, f: (MIMETypes, ObjectMapper) -> Serializer<T>)
    fun <T> registerDynamicSerializers(type: Class<T>, types: EnumSet<MIMETypes>, f: (MIMETypes, ObjectMapper) -> Serializer<T>)
    fun <T> registerDynamicSerializers(type: Class<T>, f: (Class<T>, ObjectMapper, T) -> Any)
    fun <T> registerDynamicSerializers(type: Class<T>, types: EnumSet<MIMETypes>, f: (Class<T>, ObjectMapper, T) -> Any)
    fun <T> registerSimpleDeserializers(type: Class<T>)
    fun <T> registerSimpleDeserializers(type: Class<T>, types: EnumSet<MIMETypes>)
    fun <T> registerDynamicDeserializers(type: Class<T>, f: (Class<T>, ObjectMapper, Any) -> T)
    fun <T> registerDynamicDeserializers(type: Class<T>, types: EnumSet<MIMETypes>, f: (Class<T>, ObjectMapper, Any) -> T)
    fun <T> registerDynamicDeserializers(type: Class<T>, f: (MIMETypes, ObjectMapper) -> Deserializer<T>)
    fun <T> registerDynamicDeserializers(type: Class<T>, types: EnumSet<MIMETypes>, f: (MIMETypes, ObjectMapper) -> Deserializer<T>)

    companion object {
        val default: Presentation = PresentationImpl()
    }
}