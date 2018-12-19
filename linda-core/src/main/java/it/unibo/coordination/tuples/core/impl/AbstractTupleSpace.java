package it.unibo.coordination.tuples.core.impl;

import it.unibo.coordination.tuples.core.*;
import it.unibo.coordination.tuples.core.events.OperationEvent;
import it.unibo.coordination.tuples.core.events.TupleEvent;
import it.unibo.coordination.utils.events.EventEmitter;
import it.unibo.coordination.utils.events.EventSource;
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

public abstract class AbstractTupleSpace<T extends Tuple, TT extends Template> implements ExtendedTupleSpace<T, TT>, InspectableTupleSpace<T, TT> {

    private static final boolean DEBUG = false;

    private final ExecutorService executor;
    private final String name;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final MultiSet<PendingRequest> pendingRequests = new HashMultiSet<>();

    private final EventEmitter<OperationEvent<T, TT>> operationInvoked;
    private final EventEmitter<OperationEvent<T, TT>> operationCompleted;
    private final EventEmitter<TupleEvent<T, TT>> tupleSpaceChanged;

    public AbstractTupleSpace(final String name, final ExecutorService executor) {
        this.executor = Objects.requireNonNull(executor);
        this.name = Optional.ofNullable(name).orElseGet(() -> this.getClass().getName() + "_" + System.identityHashCode(this));

        this.operationInvoked = EventEmitter.ordered(executor);
        this.operationCompleted = EventEmitter.ordered(executor);
        this.tupleSpaceChanged = EventEmitter.ordered(executor);
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

    protected final Collection<PendingRequest> getPendingRequests() {
        return pendingRequests;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CompletableFuture<T> read(final TT template) {
        var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.READ, template);
        operationInvoked.emit(invocationEvent);
        log("Invoked `read` operation on template: %s", template);
        final CompletableFuture<T> result = new CompletableFuture<>();
        result.thenAccept(tuple -> {
            operationCompleted.emit(invocationEvent.toTupleReturningCompletion(tuple));
            log("Completed `read` operation on template '%s', result: %s", template, tuple);
        });
        executor.execute(() -> this.handleRead(template, result));
        return result;
    }

    private void handleRead(final TT template, final CompletableFuture<T> promise) {
        getLock().lock();
        try {
            final Optional<T> read = lookForTuple(template);
            if (read.isPresent()) {
                promise.complete(read.get());
                onRead(read.get());
            } else {
                pendingRequests.add(newPendingAccessRequest(RequestTypes.READ, template, promise));
            }
        } finally {
            getLock().unlock();
        }
    }

    protected final MultiSet<T> lookForTuples(final TT template) {
        return lookForTuples(template, Integer.MAX_VALUE);
    }

    protected abstract MultiSet<T> lookForTuples(final TT template, int limit);

    protected abstract Optional<T> lookForTuple(final TT template);

    @Override
    public CompletableFuture<T> take(final TT template) {
        var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TAKE, template);
        operationInvoked.emit(invocationEvent);
        log("Invoked `take` operation on template: %s", template);
        final CompletableFuture<T> result = new CompletableFuture<>();
        result.thenAccept(tuple -> {
            operationCompleted.emit(invocationEvent.toTupleReturningCompletion(tuple));
            log("Completed `take` operation on template '%s', result: %s", template, tuple);
        });
        executor.execute(() -> this.handleTake(template, result));
        return result;
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
                pendingRequests.add(pendingRequest);
            }
        } finally {
            getLock().unlock();
        }
    }

    private void onTaken(T tuple) {
        tupleSpaceChanged.emit(TupleEvent.afterTaking(this, tuple));
        resumePendingAbsentRequests(tuple);
    }

    private void onRead(T tuple) {
    }

    private void onWritten(T tuple) {
        tupleSpaceChanged.emit(TupleEvent.afterWriting(this, tuple));
    }

    private MultiSet<T> retrieveTuples(TT template) {
        return retrieveTuples(template, Integer.MAX_VALUE);
    }

    protected abstract MultiSet<T> retrieveTuples(TT template, int limit);

    protected abstract Optional<T> retrieveTuple(TT template);

    @Override
    public CompletableFuture<T> write(final T tuple) {
        final var invocationEvent = OperationEvent.tupleAcceptingInvocation(this, OperationType.WRITE, tuple);
        operationInvoked.emit(invocationEvent);
        log("Invoked `write` operation for of: %s", tuple);
        final CompletableFuture<T> result = new CompletableFuture<>();
        result.thenAccept(t -> {
            operationCompleted.emit(invocationEvent.toTupleReturningCompletion(t));
            log("Completed `write` operation on tuple '%s', result: %s", tuple, t);
        });
        executor.execute(() -> this.handleWrite(tuple, result));
        return result;
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
        final Iterator<PendingRequest> i = pendingRequests.iterator();
        while (i.hasNext()) {
            final PendingRequest pendingRequest = i.next();
            if (pendingRequest.getRequestType() != RequestTypes.ABSENT
                    && pendingRequest.getTemplate().matches(insertedTuple)) {
                i.remove();
                pendingRequest.getPromiseTuple().complete(insertedTuple);
                if (pendingRequest.getRequestType() == RequestTypes.TAKE) {
                    result = Optional.empty();
                    onTaken(insertedTuple);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public CompletableFuture<MultiSet<? extends T>> get() {
        final var invocationEvent = OperationEvent.nothingAcceptingInvocation(this, OperationType.GET);
        operationInvoked.emit(invocationEvent);
        log("Invoked `get` operation");
        final CompletableFuture<MultiSet<? extends T>> result = new CompletableFuture<>();
        result.thenAccept(tuples -> {
            operationCompleted.emit(invocationEvent.toTuplesReturningCompletion(tuples));
            log("Completed `get` operation, result: %s", tuples);
        });
        executor.execute(() -> this.handleGet(result));
        return result;
    }

    private void handleGet(final CompletableFuture<MultiSet<? extends T>> promise) {
        getLock().lock();
        try {
            final MultiSet<T> result = getAllTuples();
            promise.complete(result);
        } finally {
            getLock().unlock();
        }
    }

    protected abstract MultiSet<T> getAllTuples();

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
        operationInvoked.emit(invocationEvent);
        log("Invoked `readAll` operation on template %s", template);
        final CompletableFuture<MultiSet<? extends T>> result = new CompletableFuture<>();
        result.thenAccept(tuples -> {
            operationCompleted.emit(invocationEvent.toTuplesReturningCompletion(tuples));
            log("Completed `readAll` operation on template '%s', result: %s", template, tuples);
        });
        executor.execute(() -> this.handleReadAll(template, result));
        return result;
    }

    private void handleReadAll(final TT template, final CompletableFuture<MultiSet<? extends T>> promise) {
        getLock().lock();
        try {
            final MultiSet<T> result = lookForTuples(template);
            promise.complete(result);
            result.forEach(this::onRead);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public CompletableFuture<MultiSet<? extends T>> takeAll(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TAKE_ALL, template);
        operationInvoked.emit(invocationEvent);
        log("Invoked `takeAll` operation on template %s", template);
        final CompletableFuture<MultiSet<? extends T>> result = new CompletableFuture<>();
        result.thenAccept(tuples -> {
            operationCompleted.emit(invocationEvent.toTuplesReturningCompletion(tuples));
            log("Completed `takeAll` operation on template '%s', result: %s", template, tuples);
        });
        executor.execute(() -> this.handleTakeAll(template, result));
        return result;
    }

    private void handleTakeAll(final TT template, final CompletableFuture<MultiSet<? extends T>> promise) {
        getLock().lock();
        try {
            final MultiSet<T> result = retrieveTuples(template);
            promise.complete(result);
            result.forEach(this::onTaken);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public CompletableFuture<MultiSet<? extends T>> writeAll(final Collection<? extends T> tuples) {
        final var invocationEvent = OperationEvent.tuplesAcceptingInvocation(this, OperationType.WRITE_ALL, tuples);
        operationInvoked.emit(invocationEvent);
        log("Invoked `writeAll` operation on tuples: %s", tuples);
        final CompletableFuture<MultiSet<? extends T>> result = new CompletableFuture<>();
        result.thenAccept(ts -> {
            operationCompleted.emit(invocationEvent.toTuplesReturningCompletion(ts));
            log("Completed `writeAll` operation on tuples %s, result: %s", tuples, ts);
        });
        executor.execute(() -> this.handleWriteAll(tuples, result));
        return result;
    }

    private void handleWriteAll(final Collection<? extends T> tuples, final CompletableFuture<MultiSet<? extends T>> promise) {
        getLock().lock();
        final MultiSet<T> result = new HashMultiSet<>();
        try {
            for (final T tuple : tuples) {
                result.add(tuple);
                resumePendingAccessRequests(tuple).ifPresent(this::insertTuple);
            }
            promise.complete(result);
            result.forEach(this::onWritten);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public CompletableFuture<Optional<T>> tryTake(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_TAKE, template);
        operationInvoked.emit(invocationEvent);
        log("Invoked `tryTake` operation on template: %s", template);
        final CompletableFuture<Optional<T>> result = new CompletableFuture<>();
        result.thenAccept(tuple -> {
            operationCompleted.emit(invocationEvent.toTuplesReturningCompletion(tuple.stream().collect(Collectors.toList())));
            log("Completed `tryTake` operation on template '%s', result: %s", template, tuple);
        });
        executor.execute(() -> this.handleTryTake(template, result));
        return result;
    }

    private void handleTryTake(final TT template, final CompletableFuture<Optional<T>> promise) {
        getLock().lock();
        try {
            final Optional<T> take = retrieveTuple(template);
            promise.complete(take);
            take.ifPresent(this::onTaken);
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public CompletableFuture<Optional<T>> tryRead(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_READ, template);
        operationInvoked.emit(invocationEvent);
        log("Invoked `tryRead` operation on template: %s", template);
        final CompletableFuture<Optional<T>> result = new CompletableFuture<>();
        result.thenAccept(tuple -> {
            operationCompleted.emit(invocationEvent.toTuplesReturningCompletion(tuple.stream().collect(Collectors.toList())));
            log("Completed `tryRead` operation on template '%s', result: %s", template, tuple);
        });
        executor.execute(() -> this.handleTryRead(template, result));
        return result;
    }

    private void handleTryRead(final TT template, final CompletableFuture<Optional<T>> promise) {
        getLock().lock();
        try {
            final Optional<T> take = lookForTuple(template);
            promise.complete(take);
            take.ifPresent(this::onRead);
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
        operationInvoked.emit(invocationEvent);
        log("Invoked `absent` operation on template: %s", template);
        final CompletableFuture<TT> result = new CompletableFuture<>();
        result.thenAccept(t -> {
            operationCompleted.emit(invocationEvent.toTemplateReturningCompletion(t));
            log("Completed `absent` operation on template '%s', result: %s", template, t);
        });
        executor.execute(() -> this.handleAbsent(template, result));
        return result;
    }

    private void handleAbsent(final TT template, final CompletableFuture<TT> promise) {
        getLock().lock();
        try {
            final Optional<T> read = lookForTuple(template);
            if (read.isPresent()) {
                getPendingRequests().add(newPendingAbsentRequest(template, promise));
            } else {
                promise.complete(template);
            }
        } finally {
            getLock().unlock();
        }
    }

    private void resumePendingAbsentRequests(final T removedTuple) {
        final Iterator<PendingRequest> i = getPendingRequests().iterator();
        while (i.hasNext()) {
            final PendingRequest pendingRequest = i.next();
            if (pendingRequest.getRequestType() == RequestTypes.ABSENT
                    && pendingRequest.getTemplate().matches(removedTuple)
                    && !lookForTuple(pendingRequest.getTemplate()).isPresent()) {
                i.remove();
                pendingRequest.getPromiseTemplate().complete(pendingRequest.getTemplate());
            }
        }
    }

    @Override
    public CompletableFuture<Optional<T>> tryAbsent(final TT template) {
        final var invocationEvent = OperationEvent.templateAcceptingInvocation(this, OperationType.TRY_ABSENT, template);
        operationInvoked.emit(invocationEvent);
        log("Invoked `tryAbsent` operation on template: %s", template);
        final CompletableFuture<Optional<T>> result = new CompletableFuture<>();
        result.thenAccept(tuple -> {
            operationCompleted.emit(invocationEvent.toTuplesReturningCompletion(tuple.stream().collect(Collectors.toList())));
            log("Completed `tryAbsent` operation on template '%s', result: %s", template, tuple);
        });
        executor.execute(() -> this.handleTryAbsent(template, result));
        return result;
    }

    private void handleTryAbsent(final TT template, final CompletableFuture<Optional<T>> promise) {
        handleTryRead(template, promise);
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

