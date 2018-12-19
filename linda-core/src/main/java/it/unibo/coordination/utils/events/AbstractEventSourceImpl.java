package it.unibo.coordination.utils.events;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

abstract class AbstractEventSourceImpl<T> implements EventSource<T>, EventEmitter<T> {

    private final ExecutorService engine;

    protected AbstractEventSourceImpl(ExecutorService engine) {
        this.engine = Objects.requireNonNull(engine);
    }

    protected final ExecutorService getEngine() {
        return engine;
    }

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
    public abstract void emit(T data);

    @Override
    public EventSource<T> getEventSource() {
        return this;
    }
}
