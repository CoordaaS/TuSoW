package it.unibo.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.util.*


internal class PresentationImpl : Presentation {
    private val serializers: MutableMap<Pair<Class<*>, MIMETypes>, Serializer<*>> = mutableMapOf()

    private val deserializers: MutableMap<Pair<Class<*>, MIMETypes>, Deserializer<*>> = mutableMapOf()

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

    override fun <T> register(type: Class<T>, serializer: Serializer<T>) {
        val key = type to serializer.supportedMIMEType
        require(key !in serializers) {
            "Class-MIMEType combo already registered: " + type.name + " --> " + serializer.supportedMIMEType
        }
        serializers[key] = serializer
    }

    override fun <T> register(type: Class<T>, deserializer: Deserializer<T>) {
        val key = type to deserializer.supportedMIMEType
        require(key !in deserializers) {
            "Class-MIMEType combo already registered: " + type.name + " --> " + deserializer.supportedMIMEType
        }
        deserializers[key] = deserializer
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> serializerOf(type: Class<T>, mimeType: MIMETypes): Serializer<T> {
        val key = type to mimeType
        require(key in serializers) {
            "Class-MIMEType combo not registered registered: " + type.name + " --> " + mimeType
        }
        return serializers[key]!! as Serializer<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> deserializerOf(type: Class<T>, mimeType: MIMETypes): Deserializer<T> {
        val key = type to mimeType
        require(key in deserializers) {
            "Class-MIMEType combo not registered registered: " + type.name + " --> " + mimeType
        }
        return deserializers[key]!! as Deserializer<T>
    }

    override fun <T> registerSimpleSerializers(type: Class<T>) {
        registerSimpleSerializers(type, EnumSet.allOf(MIMETypes::class.java))
    }

    override fun <T> registerSimpleSerializers(type: Class<T>, types: EnumSet<MIMETypes>) {
        for (t in types) {
            val mapper = mappers[t] ?: throw IllegalArgumentException("No mapper for MIMEType $t")
            register(type, SimpleSerializer(t, mapper))
        }
    }

    override fun <T> registerDynamicSerializers(type: Class<T>, f: (Class<T>, ObjectMapper, T) -> Any) {
        registerDynamicSerializers(type, EnumSet.allOf(MIMETypes::class.java), f)
    }

    override fun <T> registerDynamicSerializers(type: Class<T>, types: EnumSet<MIMETypes>, f: (Class<T>, ObjectMapper, T)->Any) {
        for (t in types) {
            val mapper = mappers[t] ?: throw IllegalArgumentException("No mapper for MIMEType $t")
            register(type, serializer = object : DynamicSerializer<T>(t, mapper){
                override fun toDynamicObject(`object`: T): Any =
                        f(type, mapper, `object`)
            })
        }
    }

    override fun <T> registerSimpleDeserializers(type: Class<T>) {
        registerSimpleDeserializers(type, EnumSet.allOf(MIMETypes::class.java))
    }

    override fun <T> registerSimpleDeserializers(type: Class<T>, types: EnumSet<MIMETypes>) {
        for (t in types) {
            val mapper = mappers[t] ?: throw IllegalArgumentException("No mapper for MIMEType $t")
            register(type, SimpleDeserializer(type, t, mapper))
        }
    }

    override fun <T> registerDynamicDeserializers(type: Class<T>, f: (Class<T>, ObjectMapper, Any) -> T) {
        registerDynamicDeserializers(type, EnumSet.allOf(MIMETypes::class.java), f)
    }

    override fun <T> registerDynamicDeserializers(type: Class<T>, types: EnumSet<MIMETypes>, f: (Class<T>, ObjectMapper, Any)->T) {
        for (t in types) {
            val mapper = mappers[t] ?: throw IllegalArgumentException("No mapper for MIMEType $t")
            register(type, deserializer = object : DynamicDeserializer<T>(type, t, mapper){
                override fun fromDynamicObject(dynamicObject: Any): T =
                        f(type, mapper, dynamicObject)

            })
        }
    }
}