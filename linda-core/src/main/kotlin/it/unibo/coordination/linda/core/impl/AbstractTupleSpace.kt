package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.*
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.PendingRequestEvent
import it.unibo.coordination.linda.core.events.TupleEvent
import it.unibo.coordination.utils.events.EventSource
import it.unibo.coordination.utils.events.SyncEventEmitter
import it.unibo.coordination.utils.events.filterByType
import it.unibo.coordination.utils.toMultiSet
import org.apache.commons.collections4.MultiSet
import org.apache.commons.collections4.multiset.HashMultiSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Function
import java.util.stream.Stream
import kotlin.streams.toList


abstract class AbstractTupleSpace<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
    constructor(name: String?, val executor: ExecutorService)
    : InspectableTupleSpace<T, TT, K, V, M> {

    companion object {
        @JvmStatic
        private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java.name)
    }

    override val name: String = name ?: this.javaClass.simpleName + "_" + System.identityHashCode(this)

    private val lock = ReentrantLock(true)
    
    private val tupleEventEmitter: SyncEventEmitter<TupleEvent<T, TT>> =
            SyncEventEmitter.ordered()
    override val tupleEvent: EventSource<TupleEvent<T, TT>>
        get() = tupleEventEmitter.eventSource
    
    private val operationEventEmitter: SyncEventEmitter<OperationEvent<T, TT>> =
            SyncEventEmitter.ordered()
    override val operationEvent: EventSource<OperationEvent<T, TT>>
        get() = operationEventEmitter.eventSource

    private val pendingRequestEventEmitter: SyncEventEmitter<PendingRequestEvent<T, TT>> =
            SyncEventEmitter.ordered()
    override val pendingRequestEvent: EventSource<PendingRequestEvent<T, TT>>
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

    protected fun <R> atomically(block: () -> R): R {
        try {
            lock.lock()
            return block()
        } finally {
            lock.unlock()
        }
    }

    protected fun postpone(block: () -> Unit) {
        try {
            executor.execute(block)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }

    protected fun <R> postpone(f: (Promise<R>) -> Unit): Promise<R> {
        val promise = Promise<R>()
        postpone { f(promise) }
        return promise
    }

    protected fun <T, R> postpone(f: (T, Promise<R>) -> Unit, arg: T): Promise<R> {
        val promise = Promise<R>()
        postpone { f(arg, promise) }
        return promise
    }

    protected fun log(format: String, vararg args: Any) {
        if (LOGGER.isInfoEnabled) {
            LOGGER.info(String.format("[$name] $format", *args))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AbstractTupleSpace<*, *, *, *, *>?
        return name == that!!.name && executor == that.executor
    }

    override fun hashCode(): Int {
        return Objects.hash(name, executor)
    }

    protected fun addPendingRequest(request: LocalPendingRequest<T, TT, M>) {
        pendingRequests.add(request)
    }

    protected fun <X, Y> Promise<X>.map(f: (X) -> Y): Promise<Y> {
        return thenApplyAsync(Function<X, Y> { f(it) }, executor)
    }

    override fun read(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.READ, template)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `read` operation on template: %s", template)
        return postpone(this::handleRead, template)
                .map {
                    it.also {
                        operationEventEmitter.syncEmit(invocationEvent.toTupleReturningCompletion(it.tuple.get()))
                        log("Completed `read` operation on template '%s', result: %s", template, it)
                    }
                }
    }

    private fun handleRead(template: TT, promise: Promise<M>): Unit = atomically {
        val read = lookForTuple(template)
        if (read.isMatching) {
            onReading(read.tuple.get())
            promise.complete(read)
            onRead(read.tuple.get())
        } else {
            val request = newPendingAccessRequest(RequestTypes.READ, template, promise)
            onSuspending(request)
            addPendingRequest(request)
        }
    }

    protected open fun lookForTuples(template: TT): Stream<out M> {
        return lookForTuples(template, Integer.MAX_VALUE)
    }

    protected abstract fun lookForTuples(template: TT, limit: Int): Stream<out M>

    protected abstract fun lookForTuple(template: TT): M

    override fun take(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.TAKE, template)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `take` operation on template: %s", template)
        return postpone(this::handleTake, template)
                .map {
                    it.also {
                        operationEventEmitter.syncEmit(invocationEvent.toTupleReturningCompletion(it.tuple.get()))
                        log("Completed `take` operation on template '%s', result: %s", template, it)
                    }
                }
    }

    private fun handleTake(template: TT, promise: Promise<M>): Unit = atomically {
        val take = retrieveTuple(template)
        if (take.isMatching) {
            onTaking(take.tuple.get())
            promise.complete(take)
            onTaken(take.tuple.get())
        } else {
            val pendingRequest = newPendingAccessRequest(RequestTypes.TAKE, template, promise)
            onSuspending(pendingRequest)
            addPendingRequest(pendingRequest)
        }
    }

    private fun onSuspending(request: PendingRequest<T, TT>) {
        pendingRequestEventEmitter.syncEmit(
                PendingRequestEvent.of(name, PendingRequestEvent.Effect.SUSPENDING, request)
        )
    }

    private fun onResuming(request: PendingRequest<T, TT>) {
        pendingRequestEventEmitter.syncEmit(
                PendingRequestEvent.of(name, PendingRequestEvent.Effect.RESUMING, request)
        )
    }

    private fun onTaking(tuple: T) {
        tupleEventEmitter.syncEmit(TupleEvent.beforeTaking(name, tuple))
        resumePendingAbsentRequests(tuple)
    }

    private fun onTaken(tuple: T) {
        tupleEventEmitter.syncEmit(TupleEvent.afterTaking(name, tuple))
    }

    private fun onReading(tuple: T) {
        tupleEventEmitter.syncEmit(TupleEvent.beforeReading(name, tuple))
    }

    private fun onRead(tuple: T) {
        tupleEventEmitter.syncEmit(TupleEvent.afterReading(name, tuple))
    }

    private fun onWriting(tuple: T) {
        tupleEventEmitter.syncEmit(TupleEvent.beforeWriting(name, tuple))
        resumePendingAccessRequests(tuple).ifPresent { insertTuple(it) }
    }

    private fun onWritten(tuple: T) {
        tupleEventEmitter.syncEmit(TupleEvent.afterWriting(name, tuple))
    }

    private fun onMissing(template: TT) {
        tupleEventEmitter.syncEmit(TupleEvent.beforeAbsent(name, template))
    }

    private fun onMissing(template: TT, counterExample: T) {
        tupleEventEmitter.syncEmit(TupleEvent.beforeAbsent(name, template, counterExample))
    }

    private fun onMissed(template: TT) {
        tupleEventEmitter.syncEmit(TupleEvent.afterAbsent(name, template))
    }

    private fun onMissed(template: TT, counterExample: T) {
        tupleEventEmitter.syncEmit(TupleEvent.afterAbsent(name, template, counterExample))
    }

    protected open fun retrieveTuples(template: TT): Stream<out M> {
        return retrieveTuples(template, Integer.MAX_VALUE)
    }

    override fun getAllPendingRequests(): Promise<Collection<PendingRequest<T, TT>>> {
        log("Invoked `getAllPendingRequests` operation")
        return postpone(this::handleGetAllPendingRequests).map {
            it.also {
                log("Completed `getAllPendingRequests` operation, result: %s", it)
            }
        }
    }

    private fun handleGetAllPendingRequests(result: Promise<Collection<PendingRequest<T, TT>>>) {
        val requests = pendingRequests
                .stream()
                .map { PendingRequest.wrap(it) }
                .toMultiSet()
        result.complete(requests)
    }

    protected abstract fun retrieveTuples(template: TT, limit: Int): Stream<out M>

    protected abstract fun retrieveTuple(template: TT): M

    override fun write(tuple: T): Promise<T> {
        val invocationEvent = OperationEvent.tupleAcceptingInvocation<T, TT>(name, OperationType.WRITE, tuple)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `write` operation for of: %s", tuple)
        return postpone(this::handleWrite, tuple).map {
            it.also {
                operationEventEmitter.syncEmit(invocationEvent.toTupleReturningCompletion(it))
                log("Completed `write` operation on tuple '%s', result: %s", tuple, it)
            }
        }
    }

    private fun handleWrite(tuple: T, promise: Promise<T>): Unit = atomically {
        onWriting(tuple)
//        resumePendingAccessRequests(tuple).ifPresent { insertTuple(it) }
        promise.complete(tuple)
        onWritten(tuple)
    }

    protected abstract fun match(template: TT, tuple: T): M

    protected abstract fun insertTuple(tuple: T)

    private fun resumePendingAccessRequests(insertedTuple: T): Optional<T> {
        var result = Optional.of(insertedTuple)
        val i = pendingRequestsIterator
        while (i.hasNext()) {
            val pendingRequest = i.next()
            val match = match(pendingRequest.template, insertedTuple)

            if (!match.isMatching) {
                continue
            } else if (pendingRequest.requestType != RequestTypes.ABSENT) {
                i.remove()
                onResuming(pendingRequest)
                if (pendingRequest.requestType == RequestTypes.TAKE) {
                    result = Optional.empty()
                    onTaking(insertedTuple)
                    pendingRequest.promise.complete(match)
                    onTaken(insertedTuple)
                    break
                } else if (pendingRequest.requestType == RequestTypes.READ) {
                    onReading(insertedTuple)
                    pendingRequest.promise.complete(match)
                    onRead(insertedTuple)
                } else {
                    throw IllegalStateException()
                }
            }
        }
        return result
    }

    override fun get(): Promise<Collection<T>> {
        val invocationEvent = OperationEvent.nothingAcceptingInvocation<T, TT>(name, OperationType.GET)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `get` operation")
        return postpone(this::handleGet).map { tuples ->
            tuples.also {
                operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(tuples))
                log("Completed `get` operation, result: %s", tuples)
            }
        }
    }

    private fun handleGet(promise: Promise<MultiSet<T>>): Unit = atomically {
        val result = allTuples.toMultiSet()
        result.forEach { onReading(it) }
        promise.complete(result)
        result.forEach { onRead(it) }
    }

    override fun getSize(): Promise<Int> {
        return postpone(this::handleGetSize).map {
            it.also {
                log("Completed `getSize` operation, result: %s", it)
            }
        }
    }

    protected abstract fun countTuples(): Int

    private fun handleGetSize(promise: Promise<Int>): Unit = atomically {
        val count = countTuples()
        promise.complete(count)
    }

    override fun readAll(template: TT): Promise<Collection<M>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.READ_ALL, template)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `readAll` operation on template %s", template)
        return postpone(this::handleReadAll, template).map { tuples ->
            tuples.also {
                operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(
                        tuples.stream().map { it.tuple.get() }
                ))
                log("Completed `readAll` operation on template '%s', result: %s", template, tuples)
            }
        }
    }

    private fun handleReadAll(template: TT, promise: Promise<Collection<M>>): Unit = atomically {
        val result = lookForTuples(template).toMultiSet()
        result.asSequence().map { it.tuple }.map { it.get() }.forEach { this.onReading(it) }
        promise.complete(result)
        result.asSequence().map { it.tuple }.map { it.get() }.forEach { this.onRead(it) }
    }

    override fun takeAll(template: TT): Promise<Collection<M>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.TAKE_ALL, template)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `takeAll` operation on template %s", template)
        return postpone(this::handleTakeAll, template).map { tuples ->
            tuples.also {
                operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(
                        tuples.stream().map { it.tuple }.map { it.get() }
                ))
                log("Completed `takeAll` operation on template '%s', result: %s", template, tuples)
            }
        }
    }

    private fun handleTakeAll(template: TT, promise: Promise<Collection<M>>): Unit = atomically {
        val result = retrieveTuples(template).toMultiSet()
        result.asSequence().map { it.tuple.get() }.forEach { onTaking(it) }
        promise.complete(result)
        result.asSequence().map { it.tuple.get() }.forEach { onTaken(it) }
    }

    override fun writeAll(tuples: Collection<T>): Promise<Collection<T>> {
        val invocationEvent = OperationEvent.tuplesAcceptingInvocation<T, TT>(name, OperationType.WRITE_ALL, tuples)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `writeAll` operation on tuples: %s", tuples)
        return postpone(this::handleWriteAll, tuples).map { ts ->
            ts.also {
                operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(ts))
                log("Completed `writeAll` operation on tuples %s, result: %s", tuples, ts)
            }
        }
    }

    private fun handleWriteAll(tuples: Collection<T>, promise: Promise<MultiSet<T>>): Unit = atomically {
        val result = HashMultiSet<T>()
        for (tuple in tuples) {
            result.add(tuple)
            onWriting(tuple)
//            resumePendingAccessRequests(tuple).ifPresent { this.insertTuple(it) }
        }
        promise.complete(result)
        result.forEach(this::onWritten)
    }

    override fun tryTake(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.TRY_TAKE, template)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `tryTake` operation on template: %s", template)
        return postpone(this::handleTryTake, template).map {
            it.also {
                operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(it.tuple.stream().toList()))
                log("Completed `tryTake` operation on template '%s', result: %s", template, it)
            }
        }
    }

    private fun handleTryTake(template: TT, promise: Promise<M>): Unit = atomically {
        val take = retrieveTuple(template)
        take.tuple.ifPresent { onTaking(it) }
        promise.complete(take)
        take.tuple.ifPresent { onTaken(it) }
    }

    override fun tryRead(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.TRY_READ, template)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `tryRead` operation on template: %s", template)
        return postpone(this::handleTryRead, template).map {
            it.also {
                operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(it.tuple.stream().toList()))
                log("Completed `tryRead` operation on template '%s', result: %s", template, it)
            }
        }
    }

    private fun handleTryRead(template: TT, promise: Promise<M>): Unit = atomically {
        val read = lookForTuple(template)
        read.tuple.ifPresent { onReading(it) }
        promise.complete(read)
        read.tuple.ifPresent { onRead(it) }
    }

    override fun toString(): String {
        return javaClass.name + "{" +
                "name='" + name + '\''.toString() +
                '}'.toString()
    }

    override fun absent(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.ABSENT, template)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `absent` operation on template: %s", template)
        return postpone(this::handleAbsent, template).map {
            it.also {
                operationEventEmitter.syncEmit(invocationEvent.toTemplateReturningCompletion(it.template))
                log("Completed `absent` operation on template '%s', result: %s", template, it)
            }
        }
    }

    protected abstract fun failedMatch(template: TT): M

    private fun handleAbsent(template: TT, promise: Promise<M>): Unit = atomically {
        val read = lookForTuple(template)
        if (read.isMatching) {
            val request = newPendingAbsentRequest(template, promise)
            onSuspending(request)
            addPendingRequest(request)
        } else {
            onMissing(template)
            promise.complete(failedMatch(template))
            onMissed(template)
        }
    }

    private fun resumePendingAbsentRequests(removedTuple: T) {
        val i = pendingRequestsIterator
        while (i.hasNext()) {
            val pendingRequest = i.next()
            if (pendingRequest.requestType == RequestTypes.ABSENT
                    && pendingRequest.template.matches(removedTuple)
                    && !lookForTuple(pendingRequest.template).isMatching) {

                i.remove()
                onResuming(pendingRequest)
                onMissing(pendingRequest.template)
                pendingRequest.promise.complete(failedMatch(pendingRequest.template))
                onMissed(pendingRequest.template)
            }
        }
    }

    override fun tryAbsent(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.TRY_ABSENT, template)
        operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `tryAbsent` operation on template: %s", template)
        return postpone(this::handleTryAbsent, template).map {
            it.also {
                operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(it.tuple.stream().toList()))
                log("Completed `tryAbsent` operation on template '%s', result: %s", template, it)
            }
        }
    }

    private fun handleTryAbsent(template: TT, promise: Promise<M>): Unit = atomically {
        val counterexample = lookForTuple(template)
        counterexample.tuple.ifPresent { onMissing(template, it) }
        promise.complete(counterexample)
        counterexample.tuple.ifPresent { onMissed(template, it) }
    }

    private fun newPendingAccessRequest(requestType: RequestTypes, template: TT, promise: Promise<M>): LocalPendingRequest<T, TT, M> {
        return LocalPendingRequest(requestType, template, promise)
    }

    private fun newPendingAbsentRequest(template: TT, promise: Promise<M>): LocalPendingRequest<T, TT, M> {
        return LocalPendingRequest(RequestTypes.ABSENT, template, promise)
    }
}

