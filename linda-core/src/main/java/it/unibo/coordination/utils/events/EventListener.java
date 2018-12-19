package it.unibo.coordination.utils.events;

@FunctionalInterface
public interface EventListener<Arg> {
    void onEvent(Arg data);
}
