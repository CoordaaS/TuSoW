package it.unibo.coordination.tuples.core.impl;

import it.unibo.coordination.tuples.core.*;
import it.unibo.coordination.tuples.core.events.OperationEvent;
import it.unibo.coordination.tuples.core.events.TupleEvent;
import it.unibo.coordination.utils.events.EventSource;
import it.unibo.coordination.utils.events.SyncEventEmitter;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractTupleSpace<T extends Tuple, TT extends Template> implements ExtendedTupleSpace<T, TT>, InspectableTupleSpace<T, TT> {

    private static final boolean DEBUG = true;

    private final ExecutorService executor;
    private final String name;
    private final ReentrantLock lock = new ReentrantLock(true);

    private final SyncEventEmitter<OperationEvent<T, TT>> operationInvoked;
    private final SyncEventEmitter<OperationEvent<T, TT>> operationCompleted;
    private final SyncEventEmitter<TupleEvent<T, TT>> tupleSpaceChanged;

    public AbstractTupleSpace(final String name, final ExecutorService executor) {
        this.executor = Objects.requireNonNull(executor);
        this.name = Optional.ofNullable(name).orElseGet(() -> this.getClass().getSimpleName() + "_" + System.identityHashCode(this));

        this.operationInvoked = SyncEventEmitter.ordered();
        this.operationCompleted = SyncEventEmitter.ordered();
        this.tupleSpaceChanged = SyncEventEmitter.ordered();
    }

    protected final ReentrantLock getLock() {
        return lock;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    protected final void log(final String format, final Object... args) {
        if (DEBUG) {
            System.out.println(String.format("[" + getName() + "] " + format + "\n", args));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTupleSpace<?, ?> that = (AbstractTupleSpace<?, ?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    protected abstract Collection<PendingRequest> getPendingRequests();

    protected void addPendingRequest(PendingRequest request) {
        getPendingRequests().add(request);
    }

    protected Iterator<PendingRequest> getPendingRequestsIterator() {
        return getPendingRequests().iterator();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CompletableFuture<T> read(final TT template) {
        var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.READ, template);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `read` operation on template: %s", template);
        final CompletableFuture<T> result = new CompletableFuture<>();
        executor.execute(() -> this.handleRead(template, result));
        return result.thenApplyAsync(tuple -> {
            operationCompleted.syncEmit(invocationEvent.toTupleReturningCompletion(tuple));
            log("Completed `read` operation on template '%s', result: %s", template, tuple);
            return tuple;
        }, getExecutor());
    }

    private void handleRead(final TT template, final CompletableFuture<T> promise) {
        getLock().lock();
        try {
            final Optional<T> read = lookForTuple(template);
            if (read.isPresent()) {
                promise.complete(read.get());
                onRead(read.get());
            } else {
               addPendingRequest(newPendingAccessRequest(RequestTypes.READ, template, promise));
            }
        } finally {
            getLock().unlock();
        }
    }

    protected final Stream<T> lookForTuples(final TT template) {
        return lookForTuples(template, Integer.MAX_VALUE);
    }

    protected abstract Stream<T> lookForTuples(final TT template, int limit);

    protected abstract Optional<T> lookForTuple(final TT template);

    @Override
    public CompletableFuture<T> take(final TT template) {
        var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TAKE, template);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `take` operation on template: %s", template);
        final CompletableFuture<T> result = new CompletableFuture<>();
        executor.execute(() -> this.handleTake(template, result));
        return result.thenApplyAsync(tuple -> {
            operationCompleted.syncEmit(invocationEvent.toTupleReturningCompletion(tuple));
            log("Completed `take` operation on template '%s', result: %s", template, tuple);
            return tuple;
        }, getExecutor());
    }

    private void handleTake(final TT template, final CompletableFuture<T> promise) {
        getLock().lock();
        try {
            final Optional<T> take = retrieveTuple(template);
            if (take.isPresent()) {
                promise.complete(take.get());
                onTaken(take.get());
            } else {
                final PendingRequest pendingRequest = newPendingAccessRequest(RequestTypes.TAKE, template, promise);
                addPendingRequest(pendingRequest);
            }
        } finally {
            getLock().unlock();
        }
    }

    private void onTaken(T tuple) {
        tupleSpaceChanged.syncEmit(TupleEvent.afterTaking(this, tuple));
        resumePendingAbsentRequests(tuple);
    }

    private void onRead(T tuple) {
        tupleSpaceChanged.syncEmit(TupleEvent.afterReading(this, tuple));
    }

    private void onWritten(T tuple) {
        tupleSpaceChanged.syncEmit(TupleEvent.afterWriting(this, tuple));
    }

    private void onAbsent(TT template, T counterExample) {
        tupleSpaceChanged.syncEmit(TupleEvent.afterAbsent(this, template, counterExample));
    }

    private void onAbsent(TT template) {
        tupleSpaceChanged.syncEmit(TupleEvent.afterAbsent(this, template));
    }

    private Stream<T> retrieveTuples(TT template) {
        return retrieveTuples(template, Integer.MAX_VALUE);
    }

    protected abstract Stream<T> retrieveTuples(TT template, int limit);

    protected abstract Optional<T> retrieveTuple(TT template);

    @Override
    public CompletableFuture<T> write(final T tuple) {
        final var invocationEvent = OperationEvent.tupleAcceptingInvocation(this, OperationType.WRITE, tuple);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `write` operation for of: %s", tuple);
        final CompletableFuture<T> result = new CompletableFuture<>();
        executor.execute(() -> this.handleWrite(tuple, result));
        return result.thenComposeAsync(t -> {
            operationCompleted.syncEmit(invocationEvent.toTupleReturningCompletion(t));
            log("Completed `write` operation on tuple '%s', result: %s", tuple, t);
            return CompletableFuture.completedFuture(t);
        }, executor);
    }

    private void handleWrite(final T tuple, final CompletableFuture<T> promise) {
        getLock().lock();
        try {
            resumePendingAccessRequests(tuple).ifPresent(this::insertTuple);
            promise.complete(tuple);
            onWritten(tuple);
        } finally {
            getLock().unlock();
        }
    }

    protected abstract void insertTuple(T tuple);

    private Optional<T> resumePendingAccessRequests(final T insertedTuple) {
        Optional<T> result = Optional.of(insertedTuple);
        final Iterator<PendingRequest> i = getPendingRequestsIterator();
        while (i.hasNext()) {
            final PendingRequest pendingRequest = i.next();
            if (pendingRequest.getRequestType() != RequestTypes.ABSENT
                    && pendingRequest.getTemplate().matches(insertedTuple)) {
                i.remove();
                if (pendingRequest.getRequestType() == RequestTypes.TAKE) {
                    result = Optional.empty();
                    onTaken(insertedTuple);
                    pendingRequest.getPromiseTuple().complete(insertedTuple);
                    break;
                } else if (pendingRequest.getRequestType() == RequestTypes.READ) {
                    onRead(insertedTuple);
                    pendingRequest.getPromiseTuple().complete(insertedTuple);
                } else {
                    throw new IllegalStateException();
                }
            }
        }
        return result;
    }

    @Override
    public CompletableFuture<MultiSet<? extends T>> get() {
        final var invocationEvent = OperationEvent.nothingAcceptingInvocation(this, OperationType.GET);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `get` operation");
        final CompletableFuture<MultiSet<? extends T>> result = new CompletableFuture<>();
        executor.execute(() -> this.handleGet(result));
        return result.thenApplyAsync(tuples -> {
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuples));
            log("Completed `get` operation, result: %s", tuples);
            return tuples;
        }, getExecutor());
    }

    private void handleGet(final CompletableFuture<MultiSet<? extends T>> promise) {
        getLock().lock();
        try {
            final MultiSet<T> result = getAllTuples().collect(Collectors.toCollection(HashMultiSet::new));
            promise.complete(result);
        } finally {
            getLock().unlock();
        }
    }

    protected abstract Stream<T> getAllTuples();

    @Override
    public CompletableFuture<Integer> getSize() {
        final CompletableFuture<Integer> result = new CompletableFuture<>();
        executor.execute(() -> this.handleGetSize(result));
        return result;
    }

    protected abstract int countTuples();

    private void handleGetSize(final CompletableFuture<Integer> promise) {
        getLock().lock();
        try {
            int count = countTuples();
            promise.complete(count);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public CompletableFuture<MultiSet<? extends T>> readAll(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.READ_ALL, template);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `readAll` operation on template %s", template);
        final CompletableFuture<MultiSet<? extends T>> result = new CompletableFuture<>();
        executor.execute(() -> this.handleReadAll(template, result));
        return result.thenApplyAsync(tuples -> {
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuples));
            log("Completed `readAll` operation on template '%s', result: %s", template, tuples);
            return tuples;
        }, getExecutor());
    }

    private void handleReadAll(final TT template, final CompletableFuture<MultiSet<? extends T>> promise) {
        getLock().lock();
        try {
            final MultiSet<T> result = lookForTuples(template).collect(Collectors.toCollection(HashMultiSet::new));
            promise.complete(result);
            result.forEach(this::onRead);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public CompletableFuture<MultiSet<? extends T>> takeAll(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TAKE_ALL, template);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `takeAll` operation on template %s", template);
        final CompletableFuture<MultiSet<? extends T>> result = new CompletableFuture<>();
        executor.execute(() -> this.handleTakeAll(template, result));
        return result.thenApplyAsync(tuples -> {
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuples));
            log("Completed `takeAll` operation on template '%s', result: %s", template, tuples);
            return tuples;
        }, getExecutor());
    }

    private void handleTakeAll(final TT template, final CompletableFuture<MultiSet<? extends T>> promise) {
        getLock().lock();
        try {
            final MultiSet<T> result = retrieveTuples(template).collect(Collectors.toCollection(HashMultiSet::new));
            result.forEach(this::onTaken);
            promise.complete(result);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public CompletableFuture<MultiSet<? extends T>> writeAll(final Collection<? extends T> tuples) {
        final var invocationEvent = OperationEvent.tuplesAcceptingInvocation(this, OperationType.WRITE_ALL, tuples);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `writeAll` operation on tuples: %s", tuples);
        final CompletableFuture<MultiSet<? extends T>> result = new CompletableFuture<>();
        executor.execute(() -> this.handleWriteAll(tuples, result));
        return result.thenApplyAsync(ts -> {
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(ts));
            log("Completed `writeAll` operation on tuples %s, result: %s", tuples, ts);
            return ts;
        }, getExecutor());
    }

    private void handleWriteAll(final Collection<? extends T> tuples, final CompletableFuture<MultiSet<? extends T>> promise) {
        getLock().lock();
        final MultiSet<T> result = new HashMultiSet<>();
        try {
            for (final T tuple : tuples) {
                result.add(tuple);
                resumePendingAccessRequests(tuple).ifPresent(this::insertTuple);
            }
            result.forEach(this::onWritten);
            promise.complete(result);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public CompletableFuture<Optional<T>> tryTake(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_TAKE, template);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `tryTake` operation on template: %s", template);
        final CompletableFuture<Optional<T>> result = new CompletableFuture<>();
        executor.execute(() -> this.handleTryTake(template, result));
        return result.thenApplyAsync(tuple -> {
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuple.stream().collect(Collectors.toList())));
            log("Completed `tryTake` operation on template '%s', result: %s", template, tuple);
            return tuple;
        }, getExecutor());
    }

    private void handleTryTake(final TT template, final CompletableFuture<Optional<T>> promise) {
        getLock().lock();
        try {
            final Optional<T> take = retrieveTuple(template);
            take.ifPresent(this::onTaken);
            promise.complete(take);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public CompletableFuture<Optional<T>> tryRead(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_READ, template);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `tryRead` operation on template: %s", template);
        final CompletableFuture<Optional<T>> result = new CompletableFuture<>();
        executor.execute(() -> this.handleTryRead(template, result));
        return result.thenApplyAsync(tuple -> {
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuple.stream().collect(Collectors.toList())));
            log("Completed `tryRead` operation on template '%s', result: %s", template, tuple);
            return tuple;
        }, getExecutor());
    }

    private void handleTryRead(final TT template, final CompletableFuture<Optional<T>> promise) {
        getLock().lock();
        try {
            final Optional<T> take = lookForTuple(template);
            take.ifPresent(this::onRead);
            promise.complete(take);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public CompletableFuture<TT> absent(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.ABSENT, template);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `absent` operation on template: %s", template);
        final CompletableFuture<TT> result = new CompletableFuture<>();
        executor.execute(() -> this.handleAbsent(template, result));
        return result.thenApplyAsync(t -> {
            operationCompleted.syncEmit(invocationEvent.toTemplateReturningCompletion(t));
            log("Completed `absent` operation on template '%s', result: %s", template, t);
            return t;
        }, getExecutor());
    }

    private void handleAbsent(final TT template, final CompletableFuture<TT> promise) {
        getLock().lock();
        try {
            final Optional<T> read = lookForTuple(template);
            if (read.isPresent()) {
                addPendingRequest(newPendingAbsentRequest(template, promise));
            } else {
                onAbsent(template);
                promise.complete(template);
            }
        } finally {
            getLock().unlock();
        }
    }

    private void resumePendingAbsentRequests(final T removedTuple) {
        final Iterator<PendingRequest> i = getPendingRequestsIterator();
        while (i.hasNext()) {
            final PendingRequest pendingRequest = i.next();
            if (pendingRequest.getRequestType() == RequestTypes.ABSENT
                    && pendingRequest.getTemplate().matches(removedTuple)
                    && !lookForTuple(pendingRequest.getTemplate()).isPresent()) {

                i.remove();
                onAbsent(pendingRequest.getTemplate());
                pendingRequest.getPromiseTemplate().complete(pendingRequest.getTemplate());
            }
        }
    }

    @Override
    public CompletableFuture<Optional<T>> tryAbsent(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_ABSENT, template);
        operationInvoked.syncEmit(invocationEvent);
        log("Invoked `tryAbsent` operation on template: %s", template);
        final CompletableFuture<Optional<T>> result = new CompletableFuture<>();
        executor.execute(() -> this.handleTryAbsent(template, result));
        return result.thenApplyAsync(tuple -> {
            operationCompleted.syncEmit(invocationEvent.toTuplesReturningCompletion(tuple.stream().collect(Collectors.toList())));
            log("Completed `tryAbsent` operation on template '%s', result: %s", template, tuple);
            return tuple;
        }, getExecutor());
    }

    private void handleTryAbsent(final TT template, final CompletableFuture<Optional<T>> promise) {
        getLock().lock();
        try {
            final Optional<T> counterexample = lookForTuple(template);
            counterexample.ifPresent(c -> onAbsent(template, c));
            promise.complete(counterexample);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public EventSource<OperationEvent<T, TT>> operationInvoked() {
        return operationInvoked.getEventSource();
    }

    @Override
    public EventSource<OperationEvent<T, TT>> operationCompleted() {
        return operationCompleted.getEventSource();
    }

    @Override
    public EventSource<TupleEvent<T, TT>> tupleSpaceChanged() {
        return tupleSpaceChanged.getEventSource();
    }

    private PendingRequest newPendingAccessRequest(final RequestTypes requestType, final TT template, final CompletableFuture<T> promiseTuple) {
        return new PendingRequest(requestType, template, promiseTuple, null);
    }

    private PendingRequest newPendingAbsentRequest(final TT template, final CompletableFuture<TT> promiseTemplate) {
        return new PendingRequest(RequestTypes.ABSENT, template, null, promiseTemplate);
    }

    protected enum RequestTypes {
        READ, TAKE, ABSENT
    }

    protected final class PendingRequest {
        private final RequestTypes requestType;
        private final TT template;
        private final CompletableFuture<T> promiseTuple;
        private final CompletableFuture<TT> promiseTemplate;

        private PendingRequest(final RequestTypes requestType, final TT template, final CompletableFuture<T> promiseTuple, CompletableFuture<TT> promiseTemplate) {
            this.requestType = Objects.requireNonNull(requestType);
            this.template = Objects.requireNonNull(template);
            this.promiseTuple = promiseTuple;
            this.promiseTemplate = promiseTemplate;
        }

        public RequestTypes getRequestType() {
            return requestType;
        }

        public TT getTemplate() {
            return template;
        }

        public CompletableFuture<T> getPromiseTuple() {
            return promiseTuple;
        }

        public CompletableFuture<TT> getPromiseTemplate() {
            return promiseTemplate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PendingRequest that = (PendingRequest) o;
            return requestType == that.requestType &&
                    Objects.equals(template, that.template) &&
                    Objects.equals(promiseTuple, that.promiseTuple) &&
                    Objects.equals(promiseTemplate, that.promiseTemplate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(requestType, template, promiseTuple, promiseTemplate);
        }

        @Override
        public String toString() {
            return "PendingRequest{" +
                    "requestType=" + requestType +
                    ", template=" + template +
                    ", promiseTuple=" + promiseTuple +
                    ", promiseTemplate=" + promiseTemplate +
                    '}';
        }


    }
}

