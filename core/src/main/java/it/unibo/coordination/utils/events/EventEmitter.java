package it.unibo.coordination.utils.events;

import java.util.concurrent.ExecutorService;

public interface EventEmitter<Arg> {
    static <X> EventEmitter<X> ordered(ExecutorService executor) {
        return new OrderedEventSourceImpl<>(executor);
    }

    void emit(Arg data);

    EventSource<Arg> getEventSource();

}
