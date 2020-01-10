package it.unibo.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

interface Presentation {
    @JvmDefault
    fun <T> register(type: Class<T>, serializer: Serializer<T>) {
        register(type.toTypeToken(), serializer)
    }

    fun <T> register(typeToken: TypeToken<T>, serializer: Serializer<T>)

    @JvmDefault
    fun <T> register(type: Class<T>, deserializer: Deserializer<T>) {
        register(type.toTypeToken(), deserializer)
    }
    fun <T> register(typeToken: TypeToken<T>, deserializer: Deserializer<T>)

    @JvmDefault
    fun <T> serializerOf(type: Class<T>, mimeType: MIMETypes): Serializer<T> =
            serializerOf(type.toTypeToken(), mimeType)

    fun <T> serializerOf(typeToken: TypeToken<T>, mimeType: MIMETypes): Serializer<T>

    @JvmDefault
    fun <T> deserializerOf(type: Class<T>, mimeType: MIMETypes): Deserializer<T> =
            deserializerOf(type.toTypeToken(), mimeType)

    fun <T> deserializerOf(typeToken: TypeToken<T>, mimeType: MIMETypes): Deserializer<T>

    @JvmDefault
    fun <T> registerSimpleSerializers(type: Class<T>) =
            registerSimpleSerializers(type.toTypeToken())

    @JvmDefault
    fun <T> registerSimpleSerializers(type: Class<T>, mimeTypes: EnumSet<MIMETypes>) =
            registerSimpleSerializers(type.toTypeToken(), mimeTypes)

    @JvmDefault
    fun <T> registerDynamicSerializers(type: Class<T>, f: (MIMETypes, ObjectMapper) -> Serializer<T>) =
            registerDynamicSerializers(type.toTypeToken(), f)

    @JvmDefault
    fun <T> registerDynamicSerializers(type: Class<T>, mimeTypes: EnumSet<MIMETypes>, f: (MIMETypes, ObjectMapper) -> Serializer<T>) =
            registerDynamicSerializers(type.toTypeToken(), mimeTypes, f)

    @JvmDefault
    fun <T> registerDynamicSerializers(type: Class<T>, f: (Class<T>, ObjectMapper, T) -> Any) =
            registerDynamicSerializers(type.toTypeToken()) { token, mapper, obj ->
                f(token.type, mapper, obj)
            }

    @JvmDefault
    fun <T> registerDynamicSerializers(type: Class<T>, mimeTypes: EnumSet<MIMETypes>, f: (Class<T>, ObjectMapper, T) -> Any) =
            registerDynamicSerializers(type.toTypeToken(), mimeTypes) { token, mapper, obj ->
                f(token.type, mapper, obj)
            }

    @JvmDefault
    fun <T> registerSimpleDeserializers(type: Class<T>) =
            registerSimpleDeserializers(type.toTypeToken())

    @JvmDefault
    fun <T> registerSimpleDeserializers(type: Class<T>, mimeTypes: EnumSet<MIMETypes>) =
            registerSimpleDeserializers(type.toTypeToken(), mimeTypes)

    @JvmDefault
    fun <T> registerDynamicDeserializers(type: Class<T>, f: (Class<T>, ObjectMapper, Any) -> T) =
            registerDynamicDeserializers(type.toTypeToken()) { token, mapper, obj ->
                f(token.type, mapper, obj)
            }

    @JvmDefault
    fun <T> registerDynamicDeserializers(type: Class<T>, mimeTypes: EnumSet<MIMETypes>, f: (Class<T>, ObjectMapper, Any) -> T) =
            registerDynamicDeserializers(type.toTypeToken(), mimeTypes) { token, mapper, obj ->
                f(token.type, mapper, obj)
            }

    @JvmDefault
    fun <T> registerDynamicDeserializers(type: Class<T>, f: (MIMETypes, ObjectMapper) -> Deserializer<T>) =
            registerDynamicDeserializers(type.toTypeToken(), f)

    @JvmDefault
    fun <T> registerDynamicDeserializers(type: Class<T>, mimeTypes: EnumSet<MIMETypes>, f: (MIMETypes, ObjectMapper) -> Deserializer<T>) =
            registerDynamicDeserializers(type.toTypeToken(), mimeTypes, f)

    fun <T> registerSimpleSerializers(typeToken: TypeToken<T>)
    fun <T> registerSimpleSerializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>)
    fun <T> registerDynamicSerializers(typeToken: TypeToken<T>, f: (MIMETypes, ObjectMapper) -> Serializer<T>)
    fun <T> registerDynamicSerializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>, f: (MIMETypes, ObjectMapper) -> Serializer<T>)
    fun <T> registerDynamicSerializers(typeToken: TypeToken<T>, f: (TypeToken<T>, ObjectMapper, T) -> Any)
    fun <T> registerDynamicSerializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>, f: (TypeToken<T>, ObjectMapper, T) -> Any)

    fun <T> registerSimpleDeserializers(typeToken: TypeToken<T>)
    fun <T> registerSimpleDeserializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>)
    fun <T> registerDynamicDeserializers(typeToken: TypeToken<T>, f: (TypeToken<T>, ObjectMapper, Any) -> T)
    fun <T> registerDynamicDeserializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>, f: (TypeToken<T>, ObjectMapper, Any) -> T)
    fun <T> registerDynamicDeserializers(typeToken: TypeToken<T>, f: (MIMETypes, ObjectMapper) -> Deserializer<T>)
    fun <T> registerDynamicDeserializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>, f: (MIMETypes, ObjectMapper) -> Deserializer<T>)

    fun <M : ObjectMapper> getMapper(mimeType: MIMETypes): M

    companion object Prototype : Presentation by PresentationImpl()
}