package it.unibo.coordination.utils.events;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface AsyncEventEmitter<Arg> extends SyncEventEmitter<Arg> {

    static <A> AsyncEventEmitter<A> ordered(ExecutorService executor) {
        return new AsyncOrderedEventSourceImpl<>(executor);
    }

    CompletableFuture<Arg> asyncEmit(Arg event);
}
