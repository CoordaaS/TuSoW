package it.unibo.coordination.utils.events;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

abstract class AbstractEventSourceImpl<T> implements EventSource<T>, SyncEventEmitter<T>, AsyncEventEmitter<T> {

    protected abstract Collection<EventListener<T>> getEventListeners();

    @Override
    public void bind(EventListener<T> eventListener) {
        getEventListeners().add(eventListener);
    }

    @Override
    public void unbind(EventListener<T> eventListener) {
        getEventListeners().remove(eventListener);
    }

    @Override
    public void unbindAll() {
        getEventListeners().clear();
    }

    @Override
    public abstract T syncEmit(T data);

    @Override
    public abstract CompletableFuture<T> asyncEmit(T event);

    @Override
    public EventSource<T> getEventSource() {
        return this;
    }
}
