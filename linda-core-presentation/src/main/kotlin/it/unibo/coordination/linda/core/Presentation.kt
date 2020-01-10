package it.unibo.coordination.linda.core

import it.unibo.coordination.linda.core.events.*
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.Presentation.Prototype
import it.unibo.presentation.TypeToken
import it.unibo.presentation.toTypeToken
import java.util.stream.Stream

@Suppress("UNCHECKED_CAST")
object Presentation : it.unibo.presentation.Presentation by Prototype {

    inline fun <reified T : Tuple<T>, reified TT : Template<T>> initialize() {
        val pendingRequestToken = PendingRequest::class.java.toTypeToken(T::class.java, TT::class.java) as TypeToken<PendingRequest<T, TT>>

        registerDynamicSerializers(pendingRequestToken) { mimeTypes, objectMapper ->
            PendingRequestSerializer(mimeTypes, objectMapper)
        }

        registerDynamicDeserializers(pendingRequestToken) { mimeTypes, objectMapper ->
            PendingRequestDeserializer(pendingRequestToken, mimeTypes, objectMapper)
        }

        val operationEventToken = OperationEvent::class.java.toTypeToken(T::class.java, TT::class.java) as TypeToken<OperationEvent<T, TT>>

        registerDynamicSerializers(operationEventToken) { mimeTypes, objectMapper ->
            OperationEventSerializer(mimeTypes, objectMapper)
        }

        registerDynamicDeserializers(operationEventToken) { mimeTypes, objectMapper ->
            OperationEventDeserializer(operationEventToken, mimeTypes, objectMapper)
        }

        val pendingRequestEventToken = PendingRequestEvent::class.java.toTypeToken(T::class.java, TT::class.java) as TypeToken<PendingRequestEvent<T, TT>>

        registerDynamicSerializers(pendingRequestEventToken) { mimeTypes, objectMapper ->
            PendingRequestEventSerializer(pendingRequestEventToken, mimeTypes, objectMapper)
        }

        registerDynamicDeserializers(pendingRequestEventToken) { mimeTypes, objectMapper ->
            PendingRequestEventDeserializer(pendingRequestEventToken, mimeTypes, objectMapper)
        }

        val tupleEventToken = TupleEvent::class.java.toTypeToken(T::class.java, TT::class.java) as TypeToken<TupleEvent<T, TT>>

        registerDynamicSerializers(tupleEventToken) { mimeTypes, objectMapper ->
            TupleEventSerializer(mimeTypes, objectMapper)
        }

        registerDynamicDeserializers(tupleEventToken) { mimeTypes, objectMapper ->
            TupleEventDeserializer(tupleEventToken, mimeTypes, objectMapper)
        }
    }

    internal fun <X : Any> Collection<X>.toDynamicList(mimeType: MIMETypes): List<Any>? {
        return if (isEmpty()) null
        else asSequence().map {
            Presentation.serializerOf(it.javaClass, mimeType).toDynamicObject(it)
        }.toList()
    }

    internal fun <X : Any> X?.toDynamicObject(mimeType: MIMETypes): Any? {
        return if (this == null) null
        else Presentation.serializerOf(this.javaClass, mimeType).toDynamicObject(this)
    }

    internal fun <X : Any> X?.toDynamicObject(type: Class<X>, mimeType: MIMETypes): Any? {
        return if (this == null) null
        else Presentation.serializerOf(type, mimeType).toDynamicObject(this)
    }

    internal fun <X : Any> X?.toDynamicObject(typeToken: TypeToken<X>, mimeType: MIMETypes): Any? {
        return if (this == null) null
        else Presentation.serializerOf<X>(typeToken, mimeType).toDynamicObject(this)
    }

    internal fun <X : Any> Collection<X>.toDynamicList(type: Class<X>, mimeType: MIMETypes): List<Any>? {
        return if (isEmpty()) null
        else asSequence().map {
            Presentation.serializerOf(type, mimeType).toDynamicObject(it)
        }.toList()
    }

    internal fun <X : Any> Collection<X>.toDynamicList(typeToken: TypeToken<X>, mimeType: MIMETypes): List<Any>? {
        return if (isEmpty()) null
        else asSequence().map {
            Presentation.serializerOf<X>(typeToken, mimeType).toDynamicObject(it)
        }.toList()
    }

    internal fun <X : Any> Any?.fromDynamicObject(type: Class<X>, mimeType: MIMETypes): X? {
        return if (this == null) null else deserializerOf(type, mimeType).fromDynamicObject(this)
    }

    internal fun <X : Any> Any?.fromDynamicObject(typeToken: TypeToken<X>, mimeType: MIMETypes): X? {
        return if (this == null) null else deserializerOf<X>(typeToken, mimeType).fromDynamicObject(this)
    }

    internal fun <X : Any> Any?.fromDynamicObjects(type: Class<X>, mimeType: MIMETypes): Stream<X> {
        return if (this == null) Stream.empty()
        else if (this is List<*>) {
            this.stream().map {
                if (it == null) null else deserializerOf(type, mimeType).fromDynamicObject(it)
            }.filter { it !== null }.map { it!! }
        } else {
            Stream.of(deserializerOf(type, mimeType).fromDynamicObject(this))
        }
    }

    internal fun <X : Any> Any?.fromDynamicObjects(type: TypeToken<X>, mimeType: MIMETypes): Stream<X> {
        return if (this == null) Stream.empty()
        else if (this is List<*>) {
            this.stream().map {
                if (it == null) null else deserializerOf<X>(type, mimeType).fromDynamicObject(it)
            }.filter { it !== null }.map { it!! }
        } else {
            Stream.of(deserializerOf<X>(type, mimeType).fromDynamicObject(this))
        }
    }
}