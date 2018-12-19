package it.unibo.coordination.utils.events;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class SyncOrderedEventSourceImpl<T> extends AbstractEventSourceImpl<T> {

    private final List<EventListener<T>> eventListeners = new LinkedList<>();

    @Override
    protected Collection<EventListener<T>> getEventListeners() {
        return eventListeners;
    }

    @Override
    public T syncEmit(T data) {
        for (var listener : eventListeners) {
            listener.onEvent(data);
        }
        return data;
    }

    @Override
    public CompletableFuture<T> asyncEmit(T event) {
        return CompletableFuture.completedFuture(syncEmit(event));
    }


}
