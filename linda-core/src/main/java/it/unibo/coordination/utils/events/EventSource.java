package it.unibo.coordination.utils.events;

public interface EventSource<Arg> {
    void bind(EventListener<Arg> eventListener);

    void unbind(EventListener<Arg> eventListener);

    void unbindAll();
}
