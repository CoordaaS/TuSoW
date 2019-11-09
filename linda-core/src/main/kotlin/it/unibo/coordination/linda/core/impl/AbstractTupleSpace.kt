package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.*
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.TupleEvent
import it.unibo.coordination.utils.events.SyncEventEmitter
import it.unibo.coordination.utils.toMultiSet
import org.apache.commons.collections4.MultiSet
import org.apache.commons.collections4.multiset.HashMultiSet
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Function
import java.util.stream.Stream
import kotlin.streams.toList

abstract class AbstractTupleSpace<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
    constructor(name: String?, val executor: ExecutorService)
    : InspectableTupleSpace<T, TT, K, V, M> {

    override val name: String = name ?: this.javaClass.simpleName + "_" + System.identityHashCode(this)

    private val lock = ReentrantLock(true)
    
    private val tupleSpaceChangedEmitter: SyncEventEmitter<TupleEvent<T, TT>> = SyncEventEmitter.ordered()
    override val tupleSpaceChanged
        get() = tupleSpaceChangedEmitter.eventSource
    
    private val operationCompletedEmitter: SyncEventEmitter<OperationEvent<T, TT>> = SyncEventEmitter.ordered()
    override val operationCompleted
        get() = operationCompletedEmitter.eventSource
    
    private val operationInvokedEmitter: SyncEventEmitter<OperationEvent<T, TT>> = SyncEventEmitter.ordered()
    override val operationInvoked
        get() = operationInvokedEmitter.eventSource

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
        if (DEBUG) {
            println(String.format("[$name] $format\n", *args))
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
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.READ, template)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `read` operation on template: %s", template)
        return postpone(this::handleRead, template)
                .map {
                    it.also {
                        operationCompletedEmitter.syncEmit(invocationEvent.toTupleReturningCompletion(it.tuple.get()))
                        log("Completed `read` operation on template '%s', result: %s", template, it)
                    }
                }
    }

    private fun handleRead(template: TT, promise: Promise<M>): Unit = atomically {
        val read = lookForTuple(template)
        if (read.isMatching) {
            promise.complete(read)
            onRead(read.tuple.get())
        } else {
            addPendingRequest(newPendingAccessRequest(RequestTypes.READ, template, promise))
        }
    }

    protected open fun lookForTuples(template: TT): Stream<out M> {
        return lookForTuples(template, Integer.MAX_VALUE)
    }

    protected abstract fun lookForTuples(template: TT, limit: Int): Stream<out M>

    protected abstract fun lookForTuple(template: TT): M

    override fun take(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TAKE, template)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `take` operation on template: %s", template)
        return postpone(this::handleTake, template)
                .map {
                    it.also {
                        operationCompletedEmitter.syncEmit(invocationEvent.toTupleReturningCompletion(it.tuple.get()))
                        log("Completed `take` operation on template '%s', result: %s", template, it)
                    }
                }
    }

    private fun handleTake(template: TT, promise: Promise<M>): Unit = atomically {
        val take = retrieveTuple(template)
        if (take.isMatching) {
            promise.complete(take)
            onTaken(take.tuple.get())
        } else {
            val pendingRequest = newPendingAccessRequest(RequestTypes.TAKE, template, promise)
            addPendingRequest(pendingRequest)
        }
    }

    private fun onTaken(tuple: T) {
        tupleSpaceChangedEmitter.syncEmit(TupleEvent.afterTaking(this, tuple))
        resumePendingAbsentRequests(tuple)
    }

    private fun onRead(tuple: T) {
        tupleSpaceChangedEmitter.syncEmit(TupleEvent.afterReading(this, tuple))
    }

    private fun onWritten(tuple: T) {
        tupleSpaceChangedEmitter.syncEmit(TupleEvent.afterWriting(this, tuple))
    }

    private fun onAbsent(template: TT, counterExample: T) {
        tupleSpaceChangedEmitter.syncEmit(TupleEvent.afterAbsent(this, template, counterExample))
    }

    private fun onAbsent(template: TT) {
        tupleSpaceChangedEmitter.syncEmit(TupleEvent.afterAbsent(this, template))
    }

    protected open fun retrieveTuples(template: TT): Stream<out M> {
        return retrieveTuples(template, Integer.MAX_VALUE)
    }

    protected abstract fun retrieveTuples(template: TT, limit: Int): Stream<out M>

    protected abstract fun retrieveTuple(template: TT): M

    override fun write(tuple: T): Promise<T> {
        val invocationEvent = OperationEvent.tupleAcceptingInvocation(this, OperationType.WRITE, tuple)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `write` operation for of: %s", tuple)
        return postpone(this::handleWrite, tuple).map {
            it.also {
                operationCompletedEmitter.syncEmit(invocationEvent.toTupleReturningCompletion(it))
                log("Completed `write` operation on tuple '%s', result: %s", tuple, it)
            }
        }
    }

    private fun handleWrite(tuple: T, promise: Promise<T>): Unit = atomically {
        onWritten(tuple)
        resumePendingAccessRequests(tuple).ifPresent { insertTuple(it) }
        promise.complete(tuple)
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
                if (pendingRequest.requestType == RequestTypes.TAKE) {
                    result = Optional.empty()
                    onTaken(insertedTuple)
                    pendingRequest.promise.complete(match)
                    break
                } else if (pendingRequest.requestType == RequestTypes.READ) {
                    onRead(insertedTuple)
                    pendingRequest.promise.complete(match)
                } else {
                    throw IllegalStateException()
                }
            }
        }
        return result
    }

    override fun get(): Promise<Collection<T>> {
        val invocationEvent = OperationEvent.nothingAcceptingInvocation(this, OperationType.GET)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `get` operation")
        return postpone(this::handleGet).map { tuples ->
            tuples.also {
                operationCompletedEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(tuples))
                log("Completed `get` operation, result: %s", tuples)
            }
        }
    }

    private fun handleGet(promise: Promise<MultiSet<T>>): Unit = atomically {
        val result = allTuples.toMultiSet()
        result.forEach { onRead(it) }
        promise.complete(result)
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
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.READ_ALL, template)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `readAll` operation on template %s", template)
        return postpone(this::handleReadAll, template).map { tuples ->
            tuples.also {
                operationCompletedEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(
                        tuples.stream().map { it.tuple.get() }
                ))
                log("Completed `readAll` operation on template '%s', result: %s", template, tuples)
            }
        }
    }

    private fun handleReadAll(template: TT, promise: Promise<Collection<M>>): Unit = atomically {
        val result = lookForTuples(template).toMultiSet()
        result.stream().map { it.tuple }.map<T> { it.get() }.forEach { this.onRead(it) }
        promise.complete(result)
    }

    override fun takeAll(template: TT): Promise<Collection<M>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TAKE_ALL, template)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `takeAll` operation on template %s", template)
        return postpone(this::handleTakeAll, template).map { tuples ->
            tuples.also {
                operationCompletedEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(
                        tuples.stream().map { it.tuple }.map { it.get() }
                ))
                log("Completed `takeAll` operation on template '%s', result: %s", template, tuples)
            }
        }
    }

    private fun handleTakeAll(template: TT, promise: Promise<Collection<M>>): Unit = atomically {
        val result = retrieveTuples(template).toMultiSet()
        result.stream().map { it.tuple.get() }.forEach { onTaken(it) }
        promise.complete(result)
    }

    override fun writeAll(tuples: Collection<T>): Promise<Collection<T>> {
        val invocationEvent = OperationEvent.tuplesAcceptingInvocation(this, OperationType.WRITE_ALL, tuples)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `writeAll` operation on tuples: %s", tuples)
        return postpone(this::handleWriteAll, tuples).map { ts ->
            ts.also {
                operationCompletedEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(ts))
                log("Completed `writeAll` operation on tuples %s, result: %s", tuples, ts)
            }
        }
    }

    private fun handleWriteAll(tuples: Collection<T>, promise: Promise<MultiSet<T>>): Unit = atomically {
        val result = HashMultiSet<T>()
        for (tuple in tuples) {
            result.add(tuple)
            onWritten(tuple)
            resumePendingAccessRequests(tuple).ifPresent { this.insertTuple(it) }
        }
        promise.complete(result)
    }

    override fun tryTake(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_TAKE, template)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `tryTake` operation on template: %s", template)
        return postpone(this::handleTryTake, template).map {
            it.also {
                operationCompletedEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(it.tuple.stream().toList()))
                log("Completed `tryTake` operation on template '%s', result: %s", template, it)
            }
        }
    }

    private fun handleTryTake(template: TT, promise: Promise<M>): Unit = atomically {
        val take = retrieveTuple(template)
        take.tuple.ifPresent { onTaken(it) }
        promise.complete(take)
    }

    override fun tryRead(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_READ, template)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `tryRead` operation on template: %s", template)
        return postpone(this::handleTryRead, template).map {
            it.also {
                operationCompletedEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(it.tuple.stream().toList()))
                log("Completed `tryRead` operation on template '%s', result: %s", template, it)
            }
        }
    }

    private fun handleTryRead(template: TT, promise: Promise<M>): Unit = atomically {
        val read = lookForTuple(template)
        read.tuple.ifPresent { onRead(it) }
        promise.complete(read)
    }

    override fun toString(): String {
        return javaClass.name + "{" +
                "name='" + name + '\''.toString() +
                '}'.toString()
    }

    override fun absent(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.ABSENT, template)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `absent` operation on template: %s", template)
        return postpone(this::handleAbsent, template).map {
            it.also {
                operationCompletedEmitter.syncEmit(invocationEvent.toTemplateReturningCompletion(it.template))
                log("Completed `absent` operation on template '%s', result: %s", template, it)
            }
        }
    }

    protected abstract fun failedMatch(template: TT): M

    private fun handleAbsent(template: TT, promise: Promise<M>): Unit = atomically {
        val read = lookForTuple(template)
        if (read.isMatching) {
            addPendingRequest(newPendingAbsentRequest(template, promise))
        } else {
            onAbsent(template)
            promise.complete(failedMatch(template))
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
                onAbsent(pendingRequest.template)
                pendingRequest.promise.complete(failedMatch(pendingRequest.template))
            }
        }
    }

    override fun tryAbsent(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_ABSENT, template)
        operationInvokedEmitter.syncEmit(invocationEvent)
        log("Invoked `tryAbsent` operation on template: %s", template)
        return postpone(this::handleTryAbsent, template).map {
            it.also {
                operationCompletedEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(it.tuple.stream().toList()))
                log("Completed `tryAbsent` operation on template '%s', result: %s", template, it)
            }
        }
    }

    private fun handleTryAbsent(template: TT, promise: Promise<M>): Unit = atomically {
        val counterexample = lookForTuple(template)
        counterexample.tuple.ifPresent { onAbsent(template, it) }
        promise.complete(counterexample)
    }

    private fun newPendingAccessRequest(requestType: RequestTypes, template: TT, promise: Promise<M>): LocalPendingRequest<T, TT, M> {
        return LocalPendingRequest(requestType, template, promise)
    }

    private fun newPendingAbsentRequest(template: TT, promise: Promise<M>): LocalPendingRequest<T, TT, M> {
        return LocalPendingRequest(RequestTypes.ABSENT, template, promise)
    }

    companion object {

        private val DEBUG = true

    }
}

