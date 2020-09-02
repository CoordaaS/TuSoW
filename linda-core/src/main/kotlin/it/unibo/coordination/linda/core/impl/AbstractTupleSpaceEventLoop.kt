package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.control.impl.AbstractEventLoop
import it.unibo.coordination.linda.core.*
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.PendingRequestEvent
import it.unibo.coordination.linda.core.events.TupleEvent
import it.unibo.coordination.utils.events.EventSource
import it.unibo.coordination.utils.events.SyncEventEmitter
import it.unibo.coordination.utils.events.filterByType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Stream

abstract class AbstractTupleSpaceEventLoop<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>(
        name: String? = null
) : InspectableTupleSpace<T, TT, K, V, M>, AbstractEventLoop<TupleSpaceInternalEvent<T, TT>>() {

    companion object {
        @JvmStatic
        private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java.name)
    }

    override val name: String = name ?: this.javaClass.simpleName + "_" + System.identityHashCode(this)

    private val tupleEventEmitter: SyncEventEmitter<TupleEvent<T, TT>> =
            SyncEventEmitter.ordered()
    final override val tupleEvent: EventSource<TupleEvent<T, TT>>
        get() = tupleEventEmitter.eventSource

    private val operationEventEmitter: SyncEventEmitter<OperationEvent<T, TT>> =
            SyncEventEmitter.ordered()

    final override val operationEvent: EventSource<OperationEvent<T, TT>>
        get() = operationEventEmitter.eventSource

    private val pendingRequestEventEmitter: SyncEventEmitter<PendingRequestEvent<T, TT>> =
            SyncEventEmitter.ordered()

    final override val pendingRequestEvent: EventSource<PendingRequestEvent<T, TT>>
        get() = pendingRequestEventEmitter.eventSource

    override val operationInvoked: EventSource<OperationEvent.Invocation<T, TT>> =
            operationEvent.filterByType()

    override val operationCompleted: EventSource<OperationEvent.Completion<T, TT>> =
            operationEvent.filterByType()

    override val tupleWriting: EventSource<TupleEvent.Writing<T, TT>> =
            tupleEvent.filter { it.isBefore }.filterByType()

    override val tupleReading: EventSource<TupleEvent.Reading<T, TT>> =
            tupleEvent.filter { it.isBefore }.filterByType()

    override val tupleTaking: EventSource<TupleEvent.Taking<T, TT>> =
            tupleEvent.filter { it.isBefore }.filterByType()

    override val tupleMissing: EventSource<TupleEvent.Missing<T, TT>> =
            tupleEvent.filter { it.isBefore }.filterByType()

    override val tupleWritten: EventSource<TupleEvent.Writing<T, TT>> =
            tupleEvent.filter { it.isAfter }.filterByType()

    override val tupleRead: EventSource<TupleEvent.Reading<T, TT>> =
            tupleEvent.filter { it.isAfter }.filterByType()

    override val tupleTaken: EventSource<TupleEvent.Taking<T, TT>> =
            tupleEvent.filter { it.isAfter }.filterByType()

    override val tupleMissed: EventSource<TupleEvent.Missing<T, TT>> =
            tupleEvent.filter { it.isAfter }.filterByType()

    override val pendingRequestSuspended: EventSource<PendingRequestEvent.Suspending<T, TT>> =
            pendingRequestEvent.filterByType()

    override val pendingRequestResumed: EventSource<PendingRequestEvent.Resuming<T, TT>> =
            pendingRequestEvent.filterByType()

    protected abstract val pendingRequests: MutableCollection<LocalPendingRequest<T, TT, M>>

    protected open val pendingRequestsIterator: MutableIterator<LocalPendingRequest<T, TT, M>>
        get() = pendingRequests.iterator()

    protected abstract val allTuples: Stream<T>

    protected fun log(format: String, vararg args: Any) {
        if (LOGGER.isInfoEnabled) {
            LOGGER.info(String.format("[$name] $format", *args))
        }
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
            handleInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.READ_ALL, template)
            )

    override fun takeAll(template: TT): Promise<Collection<M>> =
            handleInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TAKE_ALL, template)
            )

    override fun writeAll(tuples: Collection<T>): Promise<Collection<T>> =
            handleInvocationEventAsynchronously(
                    OperationEvent.tuplesAcceptingInvocation(name, OperationType.READ_ALL, tuples)
            )

    private fun <X> handleInvocationEventAsynchronously(event: OperationEvent.Invocation<T, TT>): Promise<X> {
        operationEventEmitter.syncEmit(event)
        val arg = when (event.argumentTemplates.size) {
            0 -> when (event.argumentTuples.size) {
                0 -> ""
                1 -> " on tuple: ${event.argumentTuple}"
                else -> " on tuples: ${event.argumentTuples}"
            }
            1 -> " on template: ${event.argumentTemplate}"
            else -> " on templates: ${event.argumentTemplates}"
        }
        log("Invoked `${event.operationType}` operation$arg")
        return Promise<X>().also { schedule(TupleSpaceInternalEvent(event, it)) }
    }

    override fun read(template: TT): Promise<M> =
            handleInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.READ, template)
            )

    override fun take(template: TT): Promise<M> =
            handleInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TAKE, template)
            )

    override fun write(tuple: T): Promise<T> =
            handleInvocationEventAsynchronously(
                    OperationEvent.tupleAcceptingInvocation(name, OperationType.WRITE, tuple)
            )

    override fun get(): Promise<Collection<T>> =
            handleInvocationEventAsynchronously(
                    OperationEvent.nothingAcceptingInvocation(name, OperationType.GET)
            )

    override fun getSize(): Promise<Int> = TODO()

    abstract override fun String.toTuple(): T

    abstract override fun String.toTemplate(): TT

    override fun tryTake(template: TT): Promise<M> =
            handleInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TRY_TAKE, template)
            )

    override fun tryRead(template: TT): Promise<M> =
            handleInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TRY_READ, template)
            )

    override fun absent(template: TT): Promise<M> =
            handleInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.ABSENT, template)
            )

    override fun tryAbsent(template: TT): Promise<M> =
            handleInvocationEventAsynchronously(
                    OperationEvent.templateAcceptingInvocation(name, OperationType.TRY_ABSENT, template)
            )

    override fun getAllPendingRequests(): Promise<Collection<PendingRequest<T, TT>>> = TODO()

    override fun onEvent(event: TupleSpaceInternalEvent<T, TT>) {
        TODO("Not yet implemented")
    }
}