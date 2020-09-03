package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.events.OperationEvent

fun <T : Tuple<T>, TT : Template<T>> OperationEvent.Invocation<T, TT>.toLogMessage(): String {
    val arg = when (argumentTemplates.size) {
        0 -> when (argumentTuples.size) {
            0 -> ""
            1 -> " on tuple: $argumentTuple"
            else -> " on tuples: $argumentTuples"
        }
        1 -> " on template: $argumentTemplate"
        else -> " on templates: $argumentTemplates"
    }
    return "Invoked `$operationType` operation$arg"
}