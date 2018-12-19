package it.unibo.coordination.utils.events;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public interface EventEmitter<Arg> {
    static <X> EventEmitter<X> ordered(ExecutorService executor) {
        return new OrderedEventSourceImpl<>(executor);
    }

    CompletableFuture<Arg> emit(Arg data);

    default Arg syncEmit(Arg data) throws ExecutionException, InterruptedException {
        return emit(data).get();
    }

    EventSource<Arg> getEventSource();

}
