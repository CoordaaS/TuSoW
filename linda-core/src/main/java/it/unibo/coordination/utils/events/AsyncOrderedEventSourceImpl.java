package it.unibo.coordination.utils.events;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

class AsyncOrderedEventSourceImpl<T> extends AbstractEventSourceImpl<T> {

    private final List<EventListener<T>> eventListeners = new LinkedList<>();
    private final ExecutorService engine;

    protected AsyncOrderedEventSourceImpl(ExecutorService engine) {
        this.engine = engine;
    }

    @Override
    protected Collection<EventListener<T>> getEventListeners() {
        return eventListeners;
    }

    @Override
    public T syncEmit(T data) {
        try {
            return asyncEmit(data).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CompletableFuture<T> asyncEmit(T event) {
        var emitterPromise = new CompletableFuture<T>();

        submitNotifications(event, List.copyOf(eventListeners), 0, emitterPromise);

        return emitterPromise;
    }

    private void submitNotifications(T data, List<EventListener<T>> eventListeners, int i, CompletableFuture<T> promise) {
        if (i == eventListeners.size()) {
            promise.complete(data);
        } else {
            engine.submit(() -> {
                eventListeners.get(i).onEvent(data);
                submitNotifications(data, eventListeners, i + 1, promise);
            });
        }
    }
}
