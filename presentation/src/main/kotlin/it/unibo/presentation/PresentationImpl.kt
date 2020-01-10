package it.unibo.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.util.*


internal class PresentationImpl : Presentation {

    private val serializers: MutableMap<Pair<TypeToken<*>, MIMETypes>, Serializer<*>> = mutableMapOf()

    private val deserializers: MutableMap<Pair<TypeToken<*>, MIMETypes>, Deserializer<*>> = mutableMapOf()

    private val mappers: MutableMap<MIMETypes, ObjectMapper> = mutableMapOf(
            MIMETypes.APPLICATION_JSON to createMapper(ObjectMapper::class.java),
            MIMETypes.APPLICATION_XML to createMapper(XmlMapper::class.java),
            MIMETypes.APPLICATION_YAML to createMapper(YAMLMapper::class.java)
    )

    protected fun <OM : ObjectMapper> createMapper(mapperClass: Class<OM>): OM {
        val mapper = mapperClass.getConstructor().newInstance()
        mapper.registerModule(JavaTimeModule())
        return mapper
    }

    override fun <T> register(typeToken: TypeToken<T>, serializer: Serializer<T>) {
        val key = typeToken to serializer.supportedMIMEType
        require(key !in serializers) {
            "Class-MIMEType combo already registered: " + typeToken.simpleName + " --> " + serializer.supportedMIMEType
        }
        serializers[key] = serializer
    }

    override fun <T> register(typeToken: TypeToken<T>, deserializer: Deserializer<T>) {
        val key = typeToken to deserializer.supportedMIMEType
        require(key !in deserializers) {
            "Class-MIMEType combo already registered: " + typeToken.simpleName + " --> " + deserializer.supportedMIMEType
        }
        deserializers[key] = deserializer
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> serializerOf(typeToken: TypeToken<T>, mimeType: MIMETypes): Serializer<T> {
        val key = typeToken to mimeType
        if (key in serializers) {
            return serializers[key]!! as Serializer<T>
        }
        val resultKey = serializers.keys
                .filter { it.second == mimeType }
                .firstOrNull { it.first.isAssignableBy(typeToken)  }
        return if (resultKey == null) {
            error("Class-MIMEType combo not registered registered: " + typeToken.simpleName + " --> " + mimeType)
        } else {
            serializers[resultKey]!! as Serializer<T>
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> deserializerOf(typeToken: TypeToken<T>, mimeType: MIMETypes): Deserializer<T> {
        val key = typeToken to mimeType
        if (key in deserializers) {
            return deserializers[key]!! as Deserializer<T>
        }
        val resultKey = deserializers.keys
                .filter { it.second == mimeType }
                .firstOrNull { it.first.isAssignableBy(typeToken)  }
        return if (resultKey == null) {
            error("Class-MIMEType combo not registered registered: " + typeToken.simpleName + " --> " + mimeType)
        } else {
            deserializers[resultKey]!! as Deserializer<T>
        }
    }

    override fun <T> registerSimpleSerializers(typeToken: TypeToken<T>) {
        registerSimpleSerializers(typeToken, MIMETypes.XML_JSON_YAML)
    }

    override fun <T> registerSimpleSerializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>) {
        for (t in mimeTypes) {
            val mapper = mappers[t] ?: throw IllegalArgumentException("No mapper for MIMEType $t")
            register(typeToken, SimpleSerializer(t, mapper))
        }
    }

    override fun <T> registerDynamicSerializers(typeToken: TypeToken<T>, f: (MIMETypes, ObjectMapper) -> Serializer<T>) {
        registerDynamicSerializers(typeToken, MIMETypes.XML_JSON_YAML, f)
    }

    override fun <T> registerDynamicSerializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>, f: (MIMETypes, ObjectMapper) -> Serializer<T>) {
        for (mime in mimeTypes) {
            register(typeToken, f(mime, mappers[mime] ?: throw IllegalArgumentException("No mapper for MIMEType $mime")))
        }
    }

    override fun <T> registerDynamicSerializers(typeToken: TypeToken<T>, f: (TypeToken<T>, ObjectMapper, T) -> Any) {
        registerDynamicSerializers(typeToken, MIMETypes.XML_JSON_YAML, f)
    }

    override fun <T> registerDynamicSerializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>, f: (TypeToken<T>, ObjectMapper, T) -> Any) {
        for (t in mimeTypes) {
            val mapper = mappers[t] ?: throw IllegalArgumentException("No mapper for MIMEType $t")
            val serializer: Serializer<T> = object : DynamicSerializer<T>(t, mapper) {
                override fun toDynamicObject(`object`: T): Any =
                        f(typeToken, mapper, `object`)
            }
            register(typeToken, serializer)
        }
    }

    override fun <T> registerSimpleDeserializers(typeToken: TypeToken<T>) {
        registerSimpleDeserializers(typeToken, MIMETypes.XML_JSON_YAML)
    }

    override fun <T> registerSimpleDeserializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>) {
        for (t in mimeTypes) {
            val mapper = mappers[t] ?: throw IllegalArgumentException("No mapper for MIMEType $t")
            register(typeToken, SimpleDeserializer(typeToken.type, t, mapper))
        }
    }

    override fun <T> registerDynamicDeserializers(typeToken: TypeToken<T>, f: (TypeToken<T>, ObjectMapper, Any) -> T) {
        registerDynamicDeserializers(typeToken, MIMETypes.XML_JSON_YAML, f)
    }

    override fun <T> registerDynamicDeserializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>, f: (TypeToken<T>, ObjectMapper, Any) -> T) {
        for (t in mimeTypes) {
            val mapper = mappers[t] ?: throw IllegalArgumentException("No mapper for MIMEType $t")
            val deserializer: Deserializer<T> = object : DynamicDeserializer<T>(typeToken.type, t, mapper) {
                override fun fromDynamicObject(dynamicObject: Any): T =
                        f(typeToken, mapper, dynamicObject)

            }
            register(typeToken.type, deserializer)
        }
    }

    override fun <T> registerDynamicDeserializers(typeToken: TypeToken<T>, f: (MIMETypes, ObjectMapper) -> Deserializer<T>) {
        registerDynamicDeserializers(typeToken, MIMETypes.XML_JSON_YAML, f)
    }

    override fun <T> registerDynamicDeserializers(typeToken: TypeToken<T>, mimeTypes: EnumSet<MIMETypes>, f: (MIMETypes, ObjectMapper) -> Deserializer<T>) {
        for (mime in mimeTypes) {
            register(typeToken, f(mime, mappers[mime] ?: throw IllegalArgumentException("No mapper for MIMEType $mime")))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <M : ObjectMapper> getMapper(mimeType: MIMETypes): M {
        return mappers[mimeType] as M
    }
}