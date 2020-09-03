package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.control.impl.AbstractEventLoop
import it.unibo.coordination.linda.core.*
import it.unibo.coordination.linda.core.OperationType.*
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.OperationEvent.*
import it.unibo.coordination.linda.core.traits.Inspectability
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

abstract class AbstractTupleSpaceEventLoop<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
@JvmOverloads
constructor(
        override val name: String = "${AbstractTupleSpaceEventLoop::class.simpleName}-${UUID.randomUUID()})",
        private val emitters: InspectabilityEmitters<T, TT> = InspectabilityEmitters(),
        private val callbacks: TupleSpaceInteralCallbacks<T, TT> = InspectabilityCallbacks(name, emitters)
) : InspectableTupleSpace<T, TT, K, V, M>,
        TupleSpaceImplementor<T, TT, K, V, M>,
        Inspectability<T, TT> by emitters,
        TupleSpaceInteralCallbacks<T, TT> by callbacks,
        AbstractEventLoop<TupleSpaceInternalEvent<T, TT>>() {

    companion object {
        @JvmStatic
        private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java.name)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun log(format: String, vararg args: Any) {
        if (LOGGER.isInfoEnabled) {
            LOGGER.info(String.format("[$name] $format", *args))
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun err(throwable: Throwable) {
        LOGGER.error(throwable.message, throwable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AbstractTupleSpaceEventLoop<*, *, *, *, *>?
        return name == that!!.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }

    override fun readAll(template: TT): Promise<Collection<M>> =
            scheduleInvocationEvent(
                    OperationEvent.templateAcceptingInvocation(name, READ_ALL, template)
            )

    override fun takeAll(template: TT): Promise<Collection<M>> =
            scheduleInvocationEvent(
                    OperationEvent.templateAcceptingInvocation(name, TAKE_ALL, template)
            )

    override fun writeAll(tuples: Collection<T>): Promise<Collection<T>> =
            scheduleInvocationEvent(
                    OperationEvent.tuplesAcceptingInvocation(name, READ_ALL, tuples)
            )

    private fun <X> scheduleInvocationEvent(event: Invocation<T, TT>): Promise<X> {
        return Promise<X>().also { schedule(TupleSpaceInternalEvent(event, it)) }
    }

    private fun rescheduleInvocationEvent(event: Invocation<T, TT>, result: Promise<*>) {
        schedule(TupleSpaceInternalEvent(event, result))
    }

    private fun scheduleCompletionEvent(event: Completion<T, TT>, result: Promise<*>) {
        schedule(TupleSpaceInternalEvent(event, result))
    }

    override fun read(template: TT): Promise<M> =
            scheduleInvocationEvent(
                    OperationEvent.templateAcceptingInvocation(name, READ, template)
            )

    override fun take(template: TT): Promise<M> =
            scheduleInvocationEvent(
                    OperationEvent.templateAcceptingInvocation(name, TAKE, template)
            )

    override fun write(tuple: T): Promise<T> =
            scheduleInvocationEvent(
                    OperationEvent.tupleAcceptingInvocation(name, WRITE, tuple)
            )

    override fun get(): Promise<Collection<T>> =
            scheduleInvocationEvent(
                    OperationEvent.nothingAcceptingInvocation(name, GET)
            )

    override fun getSize(): Promise<Int> =
            scheduleInvocationEvent(
                    OperationEvent.nothingAcceptingInvocation(name, GET_SIZE)
            )

    override fun tryTake(template: TT): Promise<M> =
            scheduleInvocationEvent(
                    OperationEvent.templateAcceptingInvocation(name, TRY_TAKE, template)
            )

    override fun tryRead(template: TT): Promise<M> =
            scheduleInvocationEvent(
                    OperationEvent.templateAcceptingInvocation(name, TRY_READ, template)
            )

    override fun absent(template: TT): Promise<M> =
            scheduleInvocationEvent(
                    OperationEvent.templateAcceptingInvocation(name, ABSENT, template)
            )

    override fun tryAbsent(template: TT): Promise<M> =
            scheduleInvocationEvent(
                    OperationEvent.templateAcceptingInvocation(name, TRY_ABSENT, template)
            )

    override fun getAllPendingRequests(): Promise<Collection<PendingRequest<T, TT>>> =
            scheduleInvocationEvent(
                    OperationEvent.nothingAcceptingInvocation(name, GET_SIZE)
            )

    override fun onEvent(event: TupleSpaceInternalEvent<T, TT>) {
        when (event.cause) {
            is Invocation<T, TT> -> handleInvocationEvent(event.cause, event.promise)
            is Completion<T, TT> -> handleCompletionEvent(event.cause, event.promise)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleInvocationEvent(event: Invocation<T, TT>, result: Promise<*>) {
        log(event.toLogMessage())
        emitters.operationEventEmitter.syncEmit(event)
        when (event.operationType) {
            GET -> handleGetInvocation(event, result as Promise<Collection<T>>)
            GET_SIZE -> handleGetSizeInvocation(event, result as Promise<Int>)
            GET_PENDING_REQUESTS -> handleGetPendingRequestsInvocation(event, result as Promise<Collection<PendingRequest<T, TT>>>)
            WRITE -> handleWriteInvocation(event, event.argumentTuple.get(), result as Promise<T>)
            READ -> handleReadInvocation(event, event.argumentTemplate.get(), result as Promise<M>)
            TAKE -> handleTakeInvocation(event, event.argumentTemplate.get(), result as Promise<M>)
            ABSENT -> handleAbsentInvocation(event, event.argumentTemplate.get(), result as Promise<M>)
            TRY_READ -> handleTryReadInvocation(event, event.argumentTemplate.get(), result as Promise<M>)
            TRY_TAKE -> handleTryTakeInvocation(event, event.argumentTemplate.get(), result as Promise<M>)
            TRY_ABSENT -> handleTryAbsentInvocation(event, event.argumentTemplate.get(), result as Promise<M>)
            WRITE_ALL -> handleWriteAllInvocation(event, event.argumentTuples, result as Promise<Collection<T>>)
            READ_ALL -> handleReadAllInvocation(event, event.argumentTemplate.get(), result as Promise<Collection<M>>)
            TAKE_ALL -> handleTakeAllInvocation(event, event.argumentTemplate.get(), result as Promise<Collection<M>>)
            else -> {
                NotImplementedError("Not supported operation: ${event.operationType}").let {
                    err(it)
                    result.completeExceptionally(it)
                    throw it
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleCompletionEvent(event: Completion<T, TT>, result: Promise<*>) {
        when (event.operationType) {
            GET -> handleGetCompletion(event, result as Promise<Collection<T>>)
            GET_SIZE -> handleGetSizeCompletion(event, result as Promise<Int>)
            GET_PENDING_REQUESTS -> handleGetPendingRequestsCompletion(event, result as Promise<Collection<PendingRequest<T, TT>>>)
            WRITE -> handleWriteCompletion(event, event.argumentTuple.get(), result as Promise<T>)
            READ -> handleReadCompletion(event, event.argumentTemplate.get(), result as Promise<M>)
            TAKE -> handleTakeCompletion(event, event.argumentTemplate.get(), result as Promise<M>)
            ABSENT -> handleAbsentCompletion(event, event.argumentTemplate.get(), result as Promise<M>)
            TRY_READ -> handleTryReadCompletion(event, event.argumentTemplate.get(), result as Promise<M>)
            TRY_TAKE -> handleTryTakeCompletion(event, event.argumentTemplate.get(), result as Promise<M>)
            TRY_ABSENT -> handleTryAbsentCompletion(event, event.argumentTemplate.get(), result as Promise<M>)
            WRITE_ALL -> handleWriteAllCompletion(event, event.argumentTuples, result as Promise<Collection<T>>)
            READ_ALL -> handleReadAllCompletion(event, event.argumentTemplate.get(), result as Promise<Collection<M>>)
            TAKE_ALL -> handleTakeAllCompletion(event, event.argumentTemplate.get(), result as Promise<Collection<M>>)
            else -> {
                NotImplementedError("Not supported operation: ${event.operationType}").let {
                    err(it)
                    result.completeExceptionally(it)
                    throw it
                }
            }
        }
        emitters.operationEventEmitter.syncEmit(event)
        log(event.toLogMessage())
    }

    private fun handleTakeAllInvocation(event: Invocation<T, TT>, template: TT, result: Promise<Collection<M>>) {

    }

    private fun handleTakeAllCompletion(event: Completion<T, TT>, template: TT, result: Promise<Collection<M>>) {
        TODO("Not yet implemented")
    }

    private fun handleReadAllInvocation(event: Invocation<T, TT>, template: TT, result: Promise<Collection<M>>) {

    }

    private fun handleReadAllCompletion(event: Completion<T, TT>, template: TT, result: Promise<Collection<M>>) {
        TODO("Not yet implemented")
    }

    private fun handleWriteAllInvocation(event: Invocation<T, TT>, tuples: List<T>, result: Promise<Collection<T>>) {

    }

    private fun handleWriteAllCompletion(event: Completion<T, TT>, tuples: List<T>, result: Promise<Collection<T>>) {
        TODO("Not yet implemented")
    }

    private fun handleTryAbsentInvocation(event: Invocation<T, TT>, template: TT, result: Promise<M>) {

    }

    private fun handleTryAbsentCompletion(event: Completion<T, TT>, template: TT, result: Promise<M>) {
        TODO("Not yet implemented")
    }

    private fun handleTryTakeInvocation(event: Invocation<T, TT>, template: TT, result: Promise<M>) {

    }

    private fun handleTryTakeCompletion(event: Completion<T, TT>, template: TT, result: Promise<M>) {
        TODO("Not yet implemented")
    }

    private fun handleTryReadInvocation(event: Invocation<T, TT>, template: TT, result: Promise<M>) {

    }

    private fun handleTryReadCompletion(event: Completion<T, TT>, template: TT, result: Promise<M>) {
        TODO("Not yet implemented")
    }

    private fun handleAbsentInvocation(event: Invocation<T, TT>, template: TT, result: Promise<M>) {

    }

    private fun handleAbsentCompletion(event: Completion<T, TT>, template: TT, result: Promise<M>) {
        TODO("Not yet implemented")
    }

    private fun handleTakeInvocation(event: Invocation<T, TT>, template: TT, result: Promise<M>) {

    }

    private fun handleTakeCompletion(event: Completion<T, TT>, template: TT, result: Promise<M>) {
        TODO("Not yet implemented")
    }

    private fun handleReadInvocation(event: Invocation<T, TT>, template: TT, result: Promise<M>) {
        val match = lookForTuple(template)
        if (match.isMatching) {
            match.tuple.get().let {
                onReading(it)
                scheduleCompletionEvent(event.toTupleReturningCompletion(it), result)
            }
        } else {
            newPendingAccessRequest(RequestTypes.READ, template, result).let {
                addPendingRequest(it)
                onSuspending(it)
            }
        }
    }

    private fun handleReadCompletion(event: Completion<T, TT>, template: TT, result: Promise<M>) {
        TODO("Not yet implemented")
    }

    private fun handleWriteInvocation(event: Invocation<T, TT>, tuple: T, result: Promise<T>) {
        onWriting(tuple)
        insertTuple(tuple)
        scheduleCompletionEvent(event.toTupleReturningCompletion(tuple), result)
    }

    private fun handleWriteCompletion(event: Completion<T, TT>, tuple: T, result: Promise<T>) {
        result.complete(tuple)
        onWritten(tuple)
    }

    private fun handleGetPendingRequestsInvocation(event: Invocation<T, TT>, result: Promise<Collection<PendingRequest<T, TT>>>) {

    }

    private fun handleGetPendingRequestsCompletion(event: Completion<T, TT>, result: Promise<Collection<PendingRequest<T, TT>>>) {
        TODO("Not yet implemented")
    }

    private fun handleGetSizeInvocation(event: Invocation<T, TT>, result: Promise<Int>) {

    }

    private fun handleGetSizeCompletion(event: Completion<T, TT>, result: Promise<Int>) {
        TODO("Not yet implemented")
    }

    private fun handleGetInvocation(event: Invocation<T, TT>, result: Promise<Collection<T>>) {

    }

    private fun handleGetCompletion(event: Completion<T, TT>, result: Promise<Collection<T>>) {
        TODO("Not yet implemented")
    }
}