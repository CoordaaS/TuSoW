package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.events.OperationEvent

private fun <T : Tuple<T>, TT : Template<T>> OperationEvent<T, TT>.onArguments(): String =
        when (argumentTemplates.size) {
            0 -> when (argumentTuples.size) {
                0 -> ""
                1 -> " on tuple: $argumentTuple"
                else -> " on tuples: $argumentTuples"
            }
            1 -> " on template: $argumentTemplate"
            else -> " on templates: $argumentTemplates"
        }

private fun <T : Tuple<T>, TT : Template<T>> OperationEvent.Completion<T, TT>.withResults(): String =
        when (results.size) {
            0 -> when (resultTemplates.size) {
                0 -> when (resultTuples.size) {
                    0 -> ""
                    1 -> ", with result: $resultTuple"
                    else -> ", with results: $resultTemplates"
                }
                1 -> ", with result: $argumentTemplate"
                else -> ", with results: $argumentTemplates"
            }
            1 -> ", with result: $result"
            else -> ", with results: $results"
        }

fun <T : Tuple<T>, TT : Template<T>> OperationEvent.Invocation<T, TT>.toLogMessage(): String {
    return "Invoked `$operationType` operation${onArguments()}"
}

fun <T : Tuple<T>, TT : Template<T>> OperationEvent.Completion<T, TT>.toLogMessage(): String {
    return "Completed `$operationType` operation${onArguments()}${withResults()}"
}

fun <T : Tuple<T>, TT : Template<T>> OperationEvent<T, TT>.toLogMessage(): String {
    return when (this) {
        is OperationEvent.Invocation<T, TT> -> toLogMessage()
        is OperationEvent.Completion<T, TT> -> toLogMessage()
    }
}