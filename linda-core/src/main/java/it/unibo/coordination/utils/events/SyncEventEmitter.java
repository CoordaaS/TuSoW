package it.unibo.coordination.utils.events;

public interface SyncEventEmitter<Arg> {

    static <X> SyncEventEmitter<X> ordered() {
        return new SyncOrderedEventSourceImpl<>();
    }

    Arg syncEmit(Arg data);

    EventSource<Arg> getEventSource();

}
