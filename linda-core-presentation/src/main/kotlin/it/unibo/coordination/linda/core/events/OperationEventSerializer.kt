package it.unibo.coordination.linda.core.events

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.coordination.linda.core.Presentation.toDynamicList
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.presentation.DynamicSerializer
import it.unibo.presentation.MIMETypes

class OperationEventSerializer<T : Tuple<T>, TT : Template<T>>(mimeType: MIMETypes, mapper: ObjectMapper)
    : DynamicSerializer<OperationEvent<T, TT>>(mimeType, mapper) {

    override fun toDynamicObject(`object`: OperationEvent<T, TT>): Any {
        return with(`object`) {
            mapOf(
                    "tupleSpaceName" to tupleSpaceName,
                    "operationPhase" to operationPhase.toString(),
                    "operationType" to operationType.toString(),
                    "argumentTuples" to argumentTuples.toDynamicList(supportedMIMEType),
                    "argumentTemplates" to argumentTemplates.toDynamicList(supportedMIMEType),
                    "resultTuples" to resultTuples.toDynamicList(supportedMIMEType),
                    "resultTemplates" to resultTemplates.toDynamicList(supportedMIMEType)
            )
        }
    }
}