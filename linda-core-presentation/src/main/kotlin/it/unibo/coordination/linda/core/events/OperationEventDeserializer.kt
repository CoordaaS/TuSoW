package it.unibo.coordination.linda.core.events

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.coordination.linda.core.OperationPhase
import it.unibo.coordination.linda.core.OperationType
import it.unibo.coordination.linda.core.Presentation.fromDynamicObjects
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.presentation.DynamicDeserializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.TypeToken
import it.unibo.presentation.toTypeToken

@Suppress("UNCHECKED_CAST")
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
            val argumentTuples = dynamicObject["argumentTuples"].fromDynamicObjects(tupleType, supportedMIMEType)
            val argumentTemplates = dynamicObject["argumentTemplates"].fromDynamicObjects(templateType, supportedMIMEType)
            val resultTuples = dynamicObject["resultTuples"].fromDynamicObjects(tupleType, supportedMIMEType)
            val resultTemplates = dynamicObject["resultTemplates"].fromDynamicObjects(templateType, supportedMIMEType)

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
}