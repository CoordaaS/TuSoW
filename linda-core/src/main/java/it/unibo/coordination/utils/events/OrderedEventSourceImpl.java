package it.unibo.coordination.utils.events;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

class OrderedEventSourceImpl<T> extends AbstractEventSourceImpl<T> {
    private List<EventListener<T>> eventListeners = new LinkedList<>();

    protected OrderedEventSourceImpl(ExecutorService engine) {
        super(engine);
    }

    @Override
    protected Collection<EventListener<T>> getEventListeners() {
        return eventListeners;
    }

    @Override
    public void emit(T data) {
        for (var listener : eventListeners) {
            getEngine().submit(() -> listener.onEvent(data));
        }
    }
}
