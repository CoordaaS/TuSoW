package it.unibo.coordination.linda.core.events

import it.unibo.coordination.linda.core.*
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

abstract class OperationEvent<T : Tuple, TT : Template>

private constructor(tupleSpace: TupleSpace<T, TT, *, *>,
                    val operationType: OperationType,
                    val operationPhase: OperationPhase,
                    argumentTuples: Stream<out T>,
                    argumentTemplates: Stream<out TT>,
                    resultTuples: Stream<out T>,
                    resultTemplates: Stream<out TT>) : TupleSpaceEvent<T, TT>(tupleSpace) {

    private val argumentTuples: List<T> = argumentTuples.toList()
    private val argumentTemplates: List<TT> = argumentTemplates.toList()
    private val resultTuples: List<T> = resultTuples.toList()
    private val resultTemplates: List<TT> = resultTemplates.toList()

    val argumentTuple: Optional<T>
        get() = argumentTuples.stream().findFirst()

    val argumentTemplate: Optional<TT>
        get() = argumentTemplates.stream().findFirst()

    val isArgumentPresent: Boolean
        get() = argumentTuples.isNotEmpty() || argumentTemplates.isNotEmpty()

    val resultTuple: Optional<T>
        get() = resultTuples.stream().findFirst()

    val resultTemplate: Optional<TT>
        get() = resultTemplates.stream().findFirst()

    val isResultPresent: Boolean
        get() = resultTuples.isNotEmpty() || resultTemplates.isNotEmpty()

    fun getArgumentTuples(): List<T> {
        return argumentTuples.toList()
    }

    fun getArgumentTemplates(): List<TT> {
        return argumentTemplates.toList()
    }

    fun getResultTuples(): List<T> {
        return resultTuples.toList()
    }

    fun getResultTemplates(): List<TT> {
        return resultTemplates.toList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        val that = other as OperationEvent<*, *>?
        return operationType == that!!.operationType &&
                operationPhase == that.operationPhase &&
                argumentTuples == that.argumentTuples &&
                argumentTemplates == that.argumentTemplates &&
                resultTuples == that.resultTuples &&
                resultTemplates == that.resultTemplates
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), operationType, operationPhase, argumentTuples, argumentTemplates, resultTuples, resultTemplates)
    }

    override fun toString(): String {
        return OperationEvent::class.java.simpleName + "." + javaClass.simpleName + "{" +
                "tupleSpace=" + tupleSpaceName +
                ", operationType=" + operationType +
                ", operationPhase=" + operationPhase +
                ", argumentTuples=" + argumentTuples +
                ", argumentTemplates=" + argumentTemplates +
                ", resultTuples=" + resultTuples +
                ", resultTemplates=" + resultTemplates +
                "}"
    }

    class Invocation<T : Tuple, TT : Template> internal constructor(tupleSpace: TupleSpace<T, TT, *, *>, operationType: OperationType, argumentTuples: Stream<out T>, argumentTemplates: Stream<out TT>) : OperationEvent<T, TT>(tupleSpace, operationType, OperationPhase.INVOCATION, argumentTuples, argumentTemplates, Stream.empty<T>(), Stream.empty<TT>()) {

        fun toTupleReturningCompletion(tuple: T): Completion<T, TT> {
            check(OperationType.isTupleReturningSet(operationType))

            return Completion(this, Stream.of(tuple), Stream.empty())
        }

        fun toTuplesReturningCompletion(vararg tuples: T): Completion<T, TT> {
            return toTuplesReturningCompletion(Stream.of(*tuples))
        }

        fun toTuplesReturningCompletion(tuples: Stream<out T>): Completion<T, TT> {
            check(OperationType.isTuplesReturningSet(operationType))

            return Completion<T, TT>(this, tuples, Stream.empty())
        }

        fun toTuplesReturningCompletion(tuples: Collection<T>): Completion<T, TT> {
            return toTuplesReturningCompletion(tuples.stream())
        }

        fun toTemplateReturningCompletion(template: TT): Completion<T, TT> {
            check(OperationType.isTemplateReturning(operationType))

            return Completion<T, TT>(this, Stream.empty(), Stream.of(template))
        }

        fun toTemplatesReturningCompletion(templates: Collection<TT>): Completion<T, TT> {
            check(OperationType.isTemplatesReturning(operationType))

            return Completion<T, TT>(this, Stream.empty(), templates.stream())
        }

        fun toCompletion(resultTuples: Stream<out T>, resultTemplates: Stream<out TT>): Completion<T, TT> {
            return Completion(this, resultTuples, resultTemplates)
        }
    }

    class Completion<T : Tuple, TT : Template> : OperationEvent<T, TT> {

        internal constructor(tupleSpace: TupleSpace<T, TT, *, *>, operationType: OperationType, argumentTuples: Stream<out T>, argumentTemplates: Stream<out TT>, resultTuples: Stream<out T>, resultTemplates: Stream<out TT>) : super(tupleSpace, operationType, OperationPhase.COMPLETION, argumentTuples, argumentTemplates, resultTuples, resultTemplates) {}

        internal constructor(invocation: Invocation<T, TT>, resultTuples: Stream<out T>, resultTemplates: Stream<out TT>) : super(
                invocation.tupleSpace,
                invocation.operationType,
                OperationPhase.COMPLETION,
                invocation.getResultTuples().stream(),
                invocation.getArgumentTemplates().stream(),
                resultTuples,
                resultTemplates
        )
    }

    companion object {

        @JvmStatic
        fun <X : Tuple, Y : Template> invocation(tupleSpace: TupleSpace<X, Y, *, *>, operationType: OperationType, argumentTuples: Stream<out X>, argumentTemplates: Stream<out Y>): Invocation<X, Y> {
            return Invocation(
                    tupleSpace, operationType, argumentTuples, argumentTemplates
            )
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> completion(tupleSpace: TupleSpace<X, Y, *, *>, operationType: OperationType, argumentTuples: Stream<out X>, argumentTemplates: Stream<out Y>, resultTuples: Stream<out X>, resultTemplates: Stream<out Y>): Completion<X, Y> {
            return Completion(
                    tupleSpace, operationType, argumentTuples, argumentTemplates, resultTuples, resultTemplates
            )
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> nothingAcceptingInvocation(tupleSpace: TupleSpace<X, Y, *, *>, operationType: OperationType): Invocation<X, Y> {
            require(OperationType.isNothingAccepting(operationType)) { operationType.toString() }

            return Invocation(
                    tupleSpace, operationType, Stream.empty(), Stream.empty()
            )
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> tupleAcceptingInvocation(tupleSpace: TupleSpace<X, Y, *, *>, operationType: OperationType, tuple: X): Invocation<X, Y> {
            require(OperationType.isTupleAcceptingSet(operationType)) { operationType.toString() }

            return Invocation(
                    tupleSpace, operationType, Stream.of(tuple), Stream.empty()
            )
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> tuplesAcceptingInvocation(tupleSpace: TupleSpace<X, Y, *, *>, operationType: OperationType, tuples: Collection<X>): Invocation<X, Y> {
            require(OperationType.isTuplesAcceptingSet(operationType)) { operationType.toString() }

            return Invocation(
                    tupleSpace, operationType, tuples.stream(), Stream.empty()
            )
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> templateAcceptingInvocation(tupleSpace: TupleSpace<X, Y, *, *>, operationType: OperationType, template: Y): Invocation<X, Y> {
            require(OperationType.isTemplateAccepting(operationType)) { operationType.toString() }

            return Invocation(
                    tupleSpace, operationType, Stream.empty(), Stream.of(template)
            )
        }

        @JvmStatic
        fun <X : Tuple, Y : Template> templatesAcceptingInvocation(tupleSpace: TupleSpace<X, Y, *, *>, operationType: OperationType, templates: Collection<Y>): Invocation<X, Y> {
            require(OperationType.isTemplatesAccepting(operationType)) { operationType.toString() }

            return Invocation(
                    tupleSpace, operationType, Stream.empty(), templates.stream()
            )
        }
    }
}