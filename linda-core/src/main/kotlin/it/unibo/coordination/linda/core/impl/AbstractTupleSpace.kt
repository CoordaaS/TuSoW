package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.*
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.traits.Inspectability
import it.unibo.coordination.utils.asStream
import it.unibo.coordination.utils.toMultiSet
import org.apache.commons.collections4.MultiSet
import org.apache.commons.collections4.multiset.HashMultiSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantLock
import kotlin.streams.toList


abstract class AbstractTupleSpace<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
@JvmOverloads
constructor(
        override val name: String = "${AbstractTupleSpace::class.simpleName}-${UUID.randomUUID()})",
        val executor: ExecutorService,
        private val emitters: InspectabilityEmitters<T, TT> = InspectabilityEmitters(),
        private val callbacks: TupleSpaceInteralCallbacks<T, TT> = InspectabilityCallbacks(name, emitters)
) : InspectableTupleSpace<T, TT, K, V, M>,
        TupleSpaceImplementor<T, TT, K, V, M>,
        Inspectability<T, TT> by emitters,
        TupleSpaceInteralCallbacks<T, TT> by callbacks {

    companion object {
        @JvmStatic
        private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java.name)
    }

    private val lock = ReentrantLock(true)

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun <R> atomically(block: () -> R): R {
        try {
            lock.lock()
            return block()
        } finally {
            lock.unlock()
        }
    }

    @Suppress("MemberVisibilityCanBePrivate", "MemberVisibilityCanBePrivate")
    protected fun postpone(block: () -> Unit) {
        try {
            executor.execute(block)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun <R> postpone(f: (Promise<R>) -> Unit): Promise<R> {
        val promise = Promise<R>()
        postpone { f(promise) }
        return promise
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun <T, R> postpone(f: (T, Promise<R>) -> Unit, arg: T): Promise<R> {
        val promise = Promise<R>()
        postpone { f(arg, promise) }
        return promise
    }

    @Suppress("MemberVisibilityCanBePrivate")
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

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun addPendingRequest(request: LocalPendingRequest<T, TT, M>) {
        pendingRequests.add(request)
    }

    protected fun <X, Y> Promise<X>.map(f: (X) -> Y): Promise<Y> {
        return thenApplyAsync({ f(it) }, executor)
    }

    override fun read(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.READ, template)
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `read` operation on template: %s", template)
        return postpone(this::handleRead, template)
                .map {
                    it.also {
                        emitters.operationEventEmitter.syncEmit(invocationEvent.toTupleReturningCompletion(it.tuple.get()))
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

    override fun take(template: TT): Promise<M> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.TAKE, template)
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `take` operation on template: %s", template)
        return postpone(this::handleTake, template)
                .map {
                    it.also {
                        emitters.operationEventEmitter.syncEmit(invocationEvent.toTupleReturningCompletion(it.tuple.get()))
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

    override fun onTaking(tuple: T) {
        callbacks.onTaking(tuple)
        resumePendingAbsentRequests(tuple)
    }

    override fun onWriting(tuple: T) {
        callbacks.onWriting(tuple)
        resumePendingAccessRequests(tuple).ifPresent { insertTuple(it) }
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

    override fun write(tuple: T): Promise<T> {
        val invocationEvent = OperationEvent.tupleAcceptingInvocation<T, TT>(name, OperationType.WRITE, tuple)
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `write` operation for of: %s", tuple)
        return postpone(this::handleWrite, tuple).map {
            it.also {
                emitters.operationEventEmitter.syncEmit(invocationEvent.toTupleReturningCompletion(it))
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
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `get` operation")
        return postpone(this::handleGet).map { tuples ->
            tuples.also {
                emitters.operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(tuples))
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

    private fun handleGetSize(promise: Promise<Int>): Unit = atomically {
        val count = countTuples()
        promise.complete(count)
    }

    override fun readAll(template: TT): Promise<Collection<M>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(name, OperationType.READ_ALL, template)
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `readAll` operation on template %s", template)
        return postpone(this::handleReadAll, template).map { tuples ->
            tuples.also {
                emitters.operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(
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
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `takeAll` operation on template %s", template)
        return postpone(this::handleTakeAll, template).map { tuples ->
            tuples.also {
                emitters.operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(
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
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `writeAll` operation on tuples: %s", tuples)
        return postpone(this::handleWriteAll, tuples).map { ts ->
            ts.also {
                emitters.operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(ts))
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
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `tryTake` operation on template: %s", template)
        return postpone(this::handleTryTake, template).map {
            it.also {
                emitters.operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(it.tuple.asStream().toList()))
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
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `tryRead` operation on template: %s", template)
        return postpone(this::handleTryRead, template).map {
            it.also {
                emitters.operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(it.tuple.asStream().toList()))
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
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `absent` operation on template: %s", template)
        return postpone(this::handleAbsent, template).map {
            it.also {
                emitters.operationEventEmitter.syncEmit(invocationEvent.toTemplateReturningCompletion(it.template))
                log("Completed `absent` operation on template '%s', result: %s", template, it)
            }
        }
    }

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
        emitters.operationEventEmitter.syncEmit(invocationEvent)
        log("Invoked `tryAbsent` operation on template: %s", template)
        return postpone(this::handleTryAbsent, template).map {
            it.also {
                emitters.operationEventEmitter.syncEmit(invocationEvent.toTuplesReturningCompletion(it.tuple.asStream().toList()))
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

