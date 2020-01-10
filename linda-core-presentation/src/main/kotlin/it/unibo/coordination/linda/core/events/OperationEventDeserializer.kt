package it.unibo.coordination.linda.core.events

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.coordination.linda.core.*
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.TypeToken
import it.unibo.presentation.toTypeToken
import java.util.stream.Stream

class OperationEventDeserializer<T : Tuple<T>, TT : Template<T>>(val tupleType: Class<T>, val templateType: Class<TT>, mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicDeserializer<OperationEvent<T, TT>>(
        OperationEvent::class.java.toTypeToken(tupleType, templateType) as TypeToken<OperationEvent<T, TT>>,
        mimeType,
        mapper
) {

    constructor(typeToken: TypeToken<OperationEvent<T, TT>>, mimeType: MIMETypes, mapper: ObjectMapper) :
            this(typeToken.genericTypes[0] as Class<T>, typeToken.genericTypes[1] as Class<TT>, mimeType, mapper)

    override fun fromDynamicObject(dynamicObject: Any): OperationEvent<T, TT> {
        if (dynamicObject is Map<*, *>) {
            val tupleSpaceName = dynamicObject["tupleSpaceName"] as String
            val operationPhase = dynamicObject["operationPhase"] as String
            val operationType = dynamicObject["operationType"] as String
            val argumentTuples = dynamicObject["argumentTuples"].fromDynamicObject(tupleType)
            val argumentTemplates = dynamicObject["argumentTemplates"].fromDynamicObject(templateType)
            val resultTuples = dynamicObject["resultTuples"].fromDynamicObject(tupleType)
            val resultTemplates = dynamicObject["resultTemplates"].fromDynamicObject(templateType)

            return OperationEvent.of(
                    tupleSpaceName,
                    OperationType.valueOf(operationType),
                    OperationPhase.valueOf(operationPhase),
                    argumentTuples,
                    argumentTemplates,
                    resultTuples,
                    resultTemplates
            )
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }

    private fun <X : Any> Any?.fromDynamicObject(type: Class<X>): Stream<X> {
        if (this == null) return Stream.empty()
        else if (this is List<*>) {
            this.stream().map {
                if (it == null) null else Presentation.deserializerOf(type, supportedMIMEType).fromDynamicObject(it)
            }.filter {
                it !== null
            }
        }
        throw IllegalArgumentException("Cannot read $supportedMIMEType")
    }
}