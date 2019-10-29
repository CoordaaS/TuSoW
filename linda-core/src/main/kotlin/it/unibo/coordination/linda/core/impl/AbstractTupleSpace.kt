package it.unibo.coordination.linda.core.impl

import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.*
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.TupleEvent
import it.unibo.coordination.utils.events.EventSource
import it.unibo.coordination.utils.events.SyncEventEmitter
import org.apache.commons.collections4.MultiSet
import org.apache.commons.collections4.multiset.HashMultiSet
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.toList

abstract class AbstractTupleSpace<T : Tuple, TT : Template, K, V>(name: String?, val executor: ExecutorService) : InspectableExtendedTupleSpace<T, TT, K, V> {

    override val name: String = name ?: this.javaClass.simpleName + "_" + System.identityHashCode(this)

    protected val lock = ReentrantLock(true)

    private val operationInvoked: SyncEventEmitter<OperationEvent<T, TT>> = SyncEventEmitter.ordered()
    private val operationCompleted: SyncEventEmitter<OperationEvent<T, TT>> = SyncEventEmitter.ordered()
    private val tupleSpaceChanged: SyncEventEmitter<TupleEvent<T, TT>> = SyncEventEmitter.ordered()

    protected fun <R> atomically(block: () -> R): R {
        try {
            lock.lock()
            return block()
        } finally {
            lock.unlock()
        }
    }

    protected fun postpone(block: () -> Unit) {
        executor.execute(block)
    }

    protected fun <T, R> postpone(f: (T, Promise<R>) -> Unit, arg: T): Promise<R> {
        val promise = Promise<R>()
        postpone { f(arg, promise) }
        return promise
    }

    protected abstract val pendingRequests: MutableCollection<PendingRequest>

    protected val pendingRequestsIterator: MutableIterator<PendingRequest>
        get() = pendingRequests.iterator()

    protected abstract val allTuples: Stream<T>

    protected fun log(format: String, vararg args: Any) {
        if (DEBUG) {
            println(String.format("[$name] $format\n", *args))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AbstractTupleSpace<*, *, *, *>?
        return name == that!!.name && executor == that.executor
    }

    override fun hashCode(): Int {
        return Objects.hash(name, executor)
    }

    protected fun addPendingRequest(request: PendingRequest) {
        pendingRequests.add(request)
    }

    protected fun <X, Y> Promise<X>.map(f: (X) -> Y): Promise<Y> {
        return thenApplyAsync(Function<X, Y> { f(it) }, executor)
    }

    override fun read(template: TT): CompletableFuture<Match<T, TT, K, V>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.READ, template)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `read` operation on template: %s", template)
        return postpone(this::handleRead, template)
                .map {
                    it.also {
                        operationCompleted.syncEmit(invocationEvent.toTupleReturningCompletion(it.tuple.get()))
                        log("Completed `read` operation on template '%s', result: %s", template, it)
                    }
                }
    }

    private fun handleRead(template: TT, promise: CompletableFuture<Match<T, TT, K, V>>): Unit = atomically {
        val read = lookForTuple(template)
        if (read.isMatching) {
            promise.complete(read)
            onRead(read.tuple.get())
        } else {
            addPendingRequest(newPendingAccessRequest(RequestTypes.READ, template, promise))
        }
    }

    protected open fun lookForTuples(template: TT): Stream<out Match<T, TT, K, V>> {
        return lookForTuples(template, Integer.MAX_VALUE)
    }

    protected abstract fun lookForTuples(template: TT, limit: Int): Stream<out Match<T, TT, K, V>>

    protected abstract fun lookForTuple(template: TT): Match<T, TT, K, V>

    override fun take(template: TT): CompletableFuture<Match<T, TT, K, V>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TAKE, template)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `take` operation on template: %s", template)
        return postpone(this::handleTake, template)
                .map {
                    it.also {
                        operationCompleted.syncEmit(invocationEvent.toTupleReturningCompletion(it.tuple.get()))
                        log("Completed `take` operation on template '%s', result: %s", template, it)
                    }
                }
    }

    private fun handleTake(template: TT, promise: CompletableFuture<Match<T, TT, K, V>>): Unit = atomically {
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
        tupleSpaceChanged.syncEmit(TupleEvent.afterTaking(this, tuple))
        resumePendingAbsentRequests(tuple)
    }

    private fun onRead(tuple: T) {
        tupleSpaceChanged.syncEmit(TupleEvent.afterReading(this, tuple))
    }

    private fun onWritten(tuple: T) {
        tupleSpaceChanged.syncEmit(TupleEvent.afterWriting(this, tuple))
    }

    private fun onAbsent(template: TT, counterExample: T) {
        tupleSpaceChanged.syncEmit(TupleEvent.afterAbsent(this, template, counterExample))
    }

    private fun onAbsent(template: TT) {
        tupleSpaceChanged.syncEmit(TupleEvent.afterAbsent(this, template))
    }

    private fun retrieveTuples(template: TT): Stream<out Match<T, TT, K, V>> {
        return retrieveTuples(template, Integer.MAX_VALUE)
    }

    protected abstract fun retrieveTuples(template: TT, limit: Int): Stream<out Match<T, TT, K, V>>

    protected abstract fun retrieveTuple(template: TT): Match<T, TT, K, V>

    override fun write(tuple: T): CompletableFuture<T> {
        val invocationEvent = OperationEvent.tupleAcceptingInvocation(this, OperationType.WRITE, tuple)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `write` operation for of: %s", tuple)
        return postpone(this::handleWrite, tuple).map {
            it.also {
                operationCompleted.syncEmit(invocationEvent.toTupleReturningCompletion(it))
                log("Completed `write` operation on tuple '%s', result: %s", tuple, it)
            }
        }
    }

    private fun handleWrite(tuple: T, promise: CompletableFuture<T>): Unit = atomically {
        onWritten(tuple)
        resumePendingAccessRequests(tuple).ifPresent { insertTuple(it) }
        promise.complete(tuple)
    }

    protected abstract fun match(template: TT, tuple: T): Match<T, TT, K, V>

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

    override fun get(): CompletableFuture<Collection<T>> {
        val invocationEvent = OperationEvent.nothingAcceptingInvocation(this, OperationType.GET)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `get` operation")
        val result = CompletableFuture<MultiSet<T>>()
        executor.execute { this.handleGet(result) }
        return result.map { tuples ->
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuples))
            log("Completed `get` operation, result: %s", tuples)
            tuples
        }
    }

    private fun handleGet(promise: CompletableFuture<MultiSet<T>>) {
        lock.lock()
        try {
            val result = allTuples.toMultiSet()
            result.stream().forEach { this.onRead(it) }
            promise.complete(result)
        } finally {
            lock.unlock()
        }
    }

    override fun getSize(): CompletableFuture<Int> {
        val result = CompletableFuture<Int>()
        executor.execute { this.handleGetSize(result) }
        return result
    }

    protected abstract fun countTuples(): Int

    private fun handleGetSize(promise: CompletableFuture<Int>) {
        lock.lock()
        try {
            val count = countTuples()
            promise.complete(count)
        } finally {
            lock.unlock()
        }
    }

    override fun readAll(template: TT): CompletableFuture<Collection<Match<T, TT, K, V>>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.READ_ALL, template)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `readAll` operation on template %s", template)
        val result = CompletableFuture<Collection<Match<T, TT, K, V>>>()
        executor.execute { this.handleReadAll(template, result) }
        return result.map { tuples ->
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(
                    tuples.stream().map { it.tuple }.map { it.get() }
            ))
            log("Completed `readAll` operation on template '%s', result: %s", template, tuples)
            tuples
        }
    }

    private fun handleReadAll(template: TT, promise: CompletableFuture<Collection<Match<T, TT, K, V>>>) {
        lock.lock()
        try {
            val result = lookForTuples(template).toList()
            result.stream().map { it.tuple }.map<T> { it.get() }.forEach { this.onRead(it) }
            promise.complete(result)
        } finally {
            lock.unlock()
        }
    }

    override fun takeAll(template: TT): CompletableFuture<Collection<Match<T, TT, K, V>>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TAKE_ALL, template)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `takeAll` operation on template %s", template)
        val result = CompletableFuture<Collection<Match<T, TT, K, V>>>()
        executor.execute { this.handleTakeAll(template, result) }
        return result.map { tuples ->
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(
                    tuples.stream().map { it.tuple }.map { it.get() }
            ))
            log("Completed `takeAll` operation on template '%s', result: %s", template, tuples)
            tuples
        }
    }

    private fun handleTakeAll(template: TT, promise: CompletableFuture<Collection<Match<T, TT, K, V>>>) {
        lock.lock()
        try {
            val result = retrieveTuples(template).toList()
            result.stream().map<Optional<T>> { it.tuple }.map<T> { it.get() }.forEach { onTaken(it) }
            promise.complete(result)
        } finally {
            lock.unlock()
        }
    }

    override fun writeAll(tuples: Collection<T>): CompletableFuture<Collection<T>> {
        val invocationEvent = OperationEvent.tuplesAcceptingInvocation(this, OperationType.WRITE_ALL, tuples)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `writeAll` operation on tuples: %s", tuples)
        val result = CompletableFuture<MultiSet<T>>()
        executor.execute { this.handleWriteAll(tuples, result) }
        return result.map { ts ->
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(ts))
            log("Completed `writeAll` operation on tuples %s, result: %s", tuples, ts)
            ts
        }
    }

    private fun handleWriteAll(tuples: Collection<T>, promise: CompletableFuture<MultiSet<T>>) {
        lock.lock()
        val result = HashMultiSet<T>()
        try {
            for (tuple in tuples) {
                result.add(tuple)
                onWritten(tuple)
                resumePendingAccessRequests(tuple).ifPresent { this.insertTuple(it) }
            }
            promise.complete(result)
        } finally {
            lock.unlock()
        }
    }

    override fun tryTake(template: TT): CompletableFuture<Match<T, TT, K, V>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_TAKE, template)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `tryTake` operation on template: %s", template)
        val result = CompletableFuture<Match<T, TT, K, V>>()
        executor.execute { this.handleTryTake(template, result) }
        return result.map { tuple ->
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuple.tuple.stream().collect(Collectors.toList<T>())))
            log("Completed `tryTake` operation on template '%s', result: %s", template, tuple)
            tuple
        }
    }

    private fun handleTryTake(template: TT, promise: CompletableFuture<Match<T, TT, K, V>>) {
        lock.lock()
        try {
            val take = retrieveTuple(template)
            take.tuple.ifPresent { onTaken(it) }
            promise.complete(take)
        } finally {
            lock.unlock()
        }
    }

    override fun tryRead(template: TT): CompletableFuture<Match<T, TT, K, V>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_READ, template)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `tryRead` operation on template: %s", template)
        val result = CompletableFuture<Match<T, TT, K, V>>()
        executor.execute { this.handleTryRead(template, result) }
        return result.map { tuple ->
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuple.tuple.stream().toList()))
            log("Completed `tryRead` operation on template '%s', result: %s", template, tuple)
            tuple
        }
    }

    private fun handleTryRead(template: TT, promise: CompletableFuture<Match<T, TT, K, V>>) {
        lock.lock()
        try {
            val read = lookForTuple(template)
            read.tuple.ifPresent { onRead(it) }
            promise.complete(read)
        } finally {
            lock.unlock()
        }
    }

    override fun toString(): String {
        return javaClass.name + "{" +
                "name='" + name + '\''.toString() +
                '}'.toString()
    }

    override fun absent(template: TT): CompletableFuture<Match<T, TT, K, V>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.ABSENT, template)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `absent` operation on template: %s", template)
        val result = CompletableFuture<Match<T, TT, K, V>>()
        executor.execute { this.handleAbsent(template, result) }
        return result.map { t ->
            operationCompleted.syncEmit(invocationEvent.toTemplateReturningCompletion(t.template))
            log("Completed `absent` operation on template '%s', result: %s", template, t)
            t
        }
    }

    protected abstract fun failedMatch(template: TT): Match<T, TT, K, V>

    private fun handleAbsent(template: TT, promise: CompletableFuture<Match<T, TT, K, V>>) {
        lock.lock()
        try {
            val read = lookForTuple(template)
            if (read.isMatching) {
                addPendingRequest(newPendingAbsentRequest(template, promise))
            } else {
                onAbsent(template)
                promise.complete(failedMatch(template))
            }
        } finally {
            lock.unlock()
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

    override fun tryAbsent(template: TT): CompletableFuture<Match<T, TT, K, V>> {
        val invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_ABSENT, template)
        operationInvoked.syncEmit(invocationEvent)
        log("Invoked `tryAbsent` operation on template: %s", template)
        val result = CompletableFuture<Match<T, TT, K, V>>()
        executor.execute { this.handleTryAbsent(template, result) }
        return result.map { tuple ->
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuple.tuple.stream().collect(Collectors.toList<T>())))
            log("Completed `tryAbsent` operation on template '%s', result: %s", template, tuple)
            tuple
        }
    }

    private fun handleTryAbsent(template: TT, promise: CompletableFuture<Match<T, TT, K, V>>) {
        lock.lock()
        try {
            val counterexample = lookForTuple(template)
            counterexample.tuple.ifPresent { c -> onAbsent(template, c) }
            promise.complete(counterexample)
        } finally {
            lock.unlock()
        }
    }

    override fun operationInvoked(): EventSource<OperationEvent<T, TT>> {
        return operationInvoked.eventSource
    }

    override fun operationCompleted(): EventSource<OperationEvent<T, TT>> {
        return operationCompleted.eventSource
    }

    override fun tupleSpaceChanged(): EventSource<TupleEvent<T, TT>> {
        return tupleSpaceChanged.eventSource
    }

    private fun newPendingAccessRequest(requestType: RequestTypes, template: TT, promise: CompletableFuture<Match<T, TT, K, V>>): PendingRequest {
        return PendingRequest(requestType, template, promise)
    }

    private fun newPendingAbsentRequest(template: TT, promise: CompletableFuture<Match<T, TT, K, V>>): PendingRequest {
        return PendingRequest(RequestTypes.ABSENT, template, promise)
    }

    protected enum class RequestTypes {
        READ, TAKE, ABSENT
    }

    protected inner class PendingRequest(requestType: RequestTypes, template: TT, promise: CompletableFuture<Match<T, TT, K, V>>) {

        val requestType: RequestTypes = Objects.requireNonNull(requestType)
        val template: TT = Objects.requireNonNull(template)
        val promise: CompletableFuture<Match<T, TT, K, V>> = Objects.requireNonNull(promise)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val that = other as AbstractTupleSpace<*, *, *, *>.PendingRequest
            return requestType == that.requestType &&
                    template == that.template &&
                    promise == that.promise
        }

        override fun hashCode(): Int {
            return Objects.hash(requestType, template, promise)
        }

        override fun toString(): String {
            return "PendingRequest{" +
                    "requestType=" + requestType +
                    ", template=" + template +
                    ", promiseTuple=" + promise +
                    '}'.toString()
        }


    }

    companion object {

        private val DEBUG = true

        protected fun <X> Stream<X>.toMultiSet(): MultiSet<X> {
            val result = HashMultiSet<X>()
            forEach {
                result.add(it)
            }
            return result
        }
    }
}

