package it.unibo.coordination.utils.events;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

class OrderedEventSourceImpl<T> extends AbstractEventSourceImpl<T> {
    private final List<EventListener<T>> eventListeners = new LinkedList<>();
//    private final ReentrantLock lock = new ReentrantLock();

    protected OrderedEventSourceImpl(ExecutorService engine) {
        super(engine);
    }

    @Override
    protected Collection<EventListener<T>> getEventListeners() {
        return eventListeners;
    }

    @Override
    public CompletableFuture<T> emit(T data) {
        var emitterPromise = new CompletableFuture<T>();

        submitNotifications(data, List.copyOf(eventListeners), 0, emitterPromise);

        return emitterPromise;
    }

    private void submitNotifications(T data, List<EventListener<T>> eventListeners, int i, CompletableFuture<T> promise) {
        if (i == eventListeners.size()) {
            promise.complete(data);
        } else {
            getEngine().submit(() -> {
                eventListeners.get(i).onEvent(data);
                submitNotifications(data, eventListeners, i + 1, promise);
            });
        }
    }
}
