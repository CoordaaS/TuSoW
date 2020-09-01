package it.unibo.coordination.testing;

import java.time.Duration;

@SuppressWarnings("unchecked")
public abstract class ActiveObject<A extends ActiveObject<A>> {
    private final String name;
    private final Thread thread;
    private volatile boolean running = true;

    public ActiveObject(final String name) {
        this.name = name + "#" + System.identityHashCode(this);
        this.thread = new Thread(this::run, this.name + "-Thread");
    }

    public ActiveObject() {
        this(ActiveObject.class.getSimpleName());
    }

    private void run() {
        try {
            onBegin();
            while (running) {
                loop();
            }
        } catch (final InterruptedException e) {
            // Silently ignores
        } catch (final Exception e) {
            onUncaughtException(e);
        } finally {
            onEnd();
        }
    }

    protected abstract void loop() throws Exception;

    protected void onBegin() throws Exception {
    }

    protected void onEnd() {
    }

    protected void onUncaughtException(final Exception e) {
        e.printStackTrace();
    }

    public A start() {
        thread.start();
        return (A) this;
    }

    public A stop() {
        running = false;
        thread.interrupt();
        return (A) this;
    }

    public A awaitTermination() {
        try {
            thread.join();
        } catch (final InterruptedException e) {
            // Do nothing
        }
        return (A) this;
    }

    protected void log(final String format, final Object... args) {
        System.out.printf("[" + getName() + "] " + format + "\n", args);
    }

    protected void sleepFor(final Duration time) throws InterruptedException {
        Thread.sleep(time.toMillis());
    }

    public String getName() {
        return name;
    }
}
