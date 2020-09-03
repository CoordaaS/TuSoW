package it.unibo.coordination.linda.core.events

import it.unibo.coordination.linda.core.OperationPhase
import it.unibo.coordination.linda.core.OperationType
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.utils.emptyMultiSet
import it.unibo.coordination.utils.multiSetOf
import it.unibo.coordination.utils.toMultiSet
import org.apache.commons.collections4.MultiSet
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

sealed class OperationEvent<T : Tuple<T>, TT : Template<T>>(
        override val tupleSpaceName: String,
        open val operationType: OperationType,
        open val operationPhase: OperationPhase,
        open val argumentTuples: List<T> = emptyList(),
        open val argumentTemplates: List<TT> = emptyList(),
        open val resultTuples: MultiSet<T> = emptyMultiSet(),
        open val resultTemplates: MultiSet<TT> = emptyMultiSet(),
        open val results: List<Any> = emptyList()
) : TupleSpaceEvent<T, TT> {

    val argumentTuple: Optional<T>
        get() = argumentTuples.stream().findFirst()

    val argumentTemplate: Optional<TT>
        get() = argumentTemplates.stream().findFirst()

    val isArgumentPresent: Boolean
        get() = argumentTuples.isNotEmpty() || argumentTemplates.isNotEmpty()

    val result: Optional<Any>
        get() = results.stream().findFirst()

    val resultTuple: Optional<T>
        get() = resultTuples.stream().findFirst()

    val resultTemplate: Optional<TT>
        get() = resultTemplates.stream().findFirst()

    val isResultPresent: Boolean
        get() = resultTuples.isNotEmpty() || resultTemplates.isNotEmpty()

    data class Invocation<T : Tuple<T>, TT : Template<T>>(
            override val tupleSpaceName: String,
            override val operationType: OperationType,
            override val argumentTuples: List<T>,
            override val argumentTemplates: List<TT>
    ) : OperationEvent<T, TT>(tupleSpaceName,
            operationType,
            OperationPhase.INVOCATION,
            argumentTuples,
            argumentTemplates) {

        private fun toCompletion(
                resultTuples: MultiSet<T> = emptyMultiSet(),
                resultTemplates: MultiSet<TT> = emptyMultiSet(),
                results: List<Any> = emptyList()
        ): Completion<T, TT> {
            return Completion(
                    tupleSpaceName = tupleSpaceName,
                    operationType = operationType,
                    argumentTuples = argumentTuples,
                    argumentTemplates = argumentTemplates,
                    resultTuples = resultTuples,
                    resultTemplates = resultTemplates,
                    results = results
            )
        }

        fun toTupleReturningCompletion(tuple: T): Completion<T, TT> {
            check(OperationType.isTupleReturningSet(operationType))

            return toCompletion(
                    resultTuples = multiSetOf(tuple)
            )
        }

        fun toTuplesReturningCompletion(vararg tuples: T): Completion<T, TT> {
            return toTuplesReturningCompletion(Stream.of(*tuples))
        }

        fun toTuplesReturningCompletion(tuples: Stream<out T>): Completion<T, TT> {
            check(OperationType.isTuplesReturningSet(operationType))

            return toCompletion(
                    resultTuples = tuples.map { it }.toMultiSet()
            )
        }

        fun toTuplesReturningCompletion(tuples: Collection<T>): Completion<T, TT> {
            return toTuplesReturningCompletion(tuples.stream())
        }

        fun toTemplateReturningCompletion(template: TT): Completion<T, TT> {
            check(OperationType.isTemplateReturning(operationType))

            return toCompletion(
                    resultTemplates = multiSetOf(template)
            )
        }

        fun toTemplatesReturningCompletion(templates: Collection<TT>): Completion<T, TT> {
            check(OperationType.isTemplatesReturning(operationType))

            return toCompletion(
                    resultTemplates = templates.toMultiSet()
            )
        }

        fun toAnyReturningCompletion(results: Collection<Any>): Completion<T, TT> {
            check(OperationType.isAnyReturning(operationType))

            return toCompletion(
                    results = results.toList()
            )
        }

        fun toCompletion(resultTuples: Stream<out T>, resultTemplates: Stream<out TT>): Completion<T, TT> {
            return toCompletion(
                    resultTuples = resultTuples.map { it }.toMultiSet(),
                    resultTemplates = resultTemplates.map { it }.toMultiSet()
            )
        }
    }

    data class Completion<T : Tuple<T>, TT : Template<T>>(
            override val tupleSpaceName: String,
            override val operationType: OperationType,
            override val argumentTuples: List<T>,
            override val argumentTemplates: List<TT>,
            override val resultTuples: MultiSet<T>,
            override val resultTemplates: MultiSet<TT>,
            override val results: List<Any>
    ) : OperationEvent<T, TT>(
            tupleSpaceName,
            operationType,
            OperationPhase.COMPLETION,
            argumentTuples,
            argumentTemplates,
            resultTuples,
            resultTemplates,
            results)

    companion object {

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> of(
                tupleSpaceName: String,
                operationType: OperationType,
                operationPhase: OperationPhase,
                argumentTuples: Stream<out X> = Stream.empty(),
                argumentTemplates: Stream<out Y> = Stream.empty(),
                resultTuples: Stream<out X> = Stream.empty(),
                resultTemplates: Stream<out Y> = Stream.empty(),
                results: Stream<Any> = Stream.empty(),
        ): OperationEvent<X, Y> {
            return when (operationPhase) {
                OperationPhase.INVOCATION -> invocation(
                        tupleSpaceName = tupleSpaceName,
                        operationType = operationType,
                        argumentTuples = argumentTuples,
                        argumentTemplates = argumentTemplates
                )
                OperationPhase.COMPLETION -> completion(
                        tupleSpaceName = tupleSpaceName,
                        operationType = operationType,
                        argumentTuples = argumentTuples,
                        argumentTemplates = argumentTemplates,
                        resultTuples = resultTuples,
                        resultTemplates = resultTemplates,
                        results = results
                )
            }
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> invocation(
                tupleSpaceName: String,
                operationType: OperationType,
                argumentTuples: Stream<out X> = Stream.empty(),
                argumentTemplates: Stream<out Y> = Stream.empty()
        ): Invocation<X, Y> {
            return Invocation(
                    tupleSpaceName = tupleSpaceName,
                    operationType = operationType,
                    argumentTuples = argumentTuples.toList(),
                    argumentTemplates = argumentTemplates.toList()
            )
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> completion(
                tupleSpaceName: String,
                operationType: OperationType,
                argumentTuples: Stream<out X> = Stream.empty(),
                argumentTemplates: Stream<out Y> = Stream.empty(),
                resultTuples: Stream<out X> = Stream.empty(),
                resultTemplates: Stream<out Y> = Stream.empty(),
                results: Stream<out Any> = Stream.empty()
        ): Completion<X, Y> {
            return Completion(
                    tupleSpaceName = tupleSpaceName,
                    operationType = operationType,
                    argumentTuples = argumentTuples.toList(),
                    argumentTemplates = argumentTemplates.toList(),
                    resultTuples = resultTuples.map { it }.toMultiSet(),
                    resultTemplates = resultTemplates.map { it }.toMultiSet(),
                    results = results.toList()
            )
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> nothingAcceptingInvocation(tupleSpaceName: String, operationType: OperationType): Invocation<X, Y> {
            require(OperationType.isNothingAccepting(operationType)) { operationType.toString() }

            return invocation(
                    tupleSpaceName = tupleSpaceName,
                    operationType = operationType
            )
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> tupleAcceptingInvocation(tupleSpaceName: String, operationType: OperationType, tuple: X): Invocation<X, Y> {
            require(OperationType.isTupleAcceptingSet(operationType)) { operationType.toString() }

            return invocation(
                    tupleSpaceName = tupleSpaceName,
                    operationType = operationType,
                    argumentTuples = Stream.of(tuple)
            )
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> tuplesAcceptingInvocation(tupleSpaceName: String, operationType: OperationType, tuples: Collection<X>): Invocation<X, Y> {
            require(OperationType.isTuplesAcceptingSet(operationType)) { operationType.toString() }

            return invocation(
                    tupleSpaceName = tupleSpaceName,
                    operationType = operationType,
                    argumentTuples = tuples.stream()
            )
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> templateAcceptingInvocation(tupleSpaceName: String, operationType: OperationType, template: Y): Invocation<X, Y> {
            require(OperationType.isTemplateAccepting(operationType)) { operationType.toString() }

            return invocation(
                    tupleSpaceName = tupleSpaceName,
                    operationType = operationType,
                    argumentTemplates = Stream.of(template)
            )
        }

        @JvmStatic
        fun <X : Tuple<X>, Y : Template<X>> templatesAcceptingInvocation(tupleSpaceName: String, operationType: OperationType, templates: Collection<Y>): Invocation<X, Y> {
            require(OperationType.isTemplatesAccepting(operationType)) { operationType.toString() }

            return invocation(
                    tupleSpaceName = tupleSpaceName,
                    operationType = operationType,
                    argumentTemplates = templates.stream()
            )
        }
    }
}