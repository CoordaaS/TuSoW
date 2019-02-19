package it.unibo.coordination.flow;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

public abstract class BaseAgent {

    private final CompletableFuture<Void> termination = new CompletableFuture<>();
    private AgentId id;
    private Status state = Status.CREATED;
    private Continuation nextOperation = null;
    private Environment environment;

    public BaseAgent(String name) {
        name = Optional.ofNullable(name).orElseGet(() -> getClass().getSimpleName() + "_" + System.identityHashCode(BaseAgent.this));
        id = new AgentId(name);
    }

    public BaseAgent(AgentId id) {
        this.id = Objects.requireNonNull(id);
    }

    private void ensureCurrentStateIs(Status state) {
        ensureCurrentStateIs(EnumSet.of(state));
    }

    private void ensureCurrentStateIs(EnumSet<Status> states) {
        if (!currentStateIsOneOf(states)) {
            throw new IllegalStateException("Illegal state: " + this.state + ", expected: " + states);
        }
    }

    private boolean currentStateIs(Status state) {
        return Objects.equals(this.state, state);
    }

    private boolean currentStateIsOneOf(EnumSet<Status> states) {
        return states.contains(this.state);
    }

    private void doStateTransition(Continuation whatToDo) {
        switch (state) {
            case CREATED:
                doStateTransitionFromCreated(whatToDo);
                break;
            case STARTED:
                doStateTransitionFromStarted(whatToDo);
                break;
            case RUNNING:
                doStateTransitionFromRunning(whatToDo);
                break;
            case PAUSED:
                doStateTransitionFromPaused(whatToDo);
                break;
            case STOPPED:
                doStateTransitionFromStopped(whatToDo);
                break;
            default:
                throw new IllegalStateException("Illegal state: " + state);

        }
    }

    protected void doStateTransitionFromCreated(Continuation whatToDo) {
        switch (whatToDo) {
            case CONTINUE:
                state = Status.STARTED;
                doBegin();
                break;
            default:
                throw new IllegalArgumentException("Unexpected transition: " + state + " -" + whatToDo + "-> ???");
        }
    }

    protected void doStateTransitionFromStarted(Continuation whatToDo) {
        doStateTransitionFromRunning(whatToDo);
    }

    protected void doStateTransitionFromRunning(Continuation whatToDo) {
        switch (whatToDo) {
            case PAUSE:
                state = Status.PAUSED;
                break;
            case RESTART:
                state = Status.STARTED;
                doBegin();
                break;
            case STOP:
                state = Status.STOPPED;
                doEnd();
                break;
            case CONTINUE:
                state = Status.RUNNING;
                doRun();
                break;
            default:
                throw new IllegalArgumentException("Unexpected transition: " + state + " -" + whatToDo + "-> ???");
        }
    }

    protected void doStateTransitionFromPaused(Continuation whatToDo) {
        doStateTransitionFromRunning(whatToDo);
    }

    protected void doStateTransitionFromStopped(Continuation whatToDo) {
        switch (whatToDo) {
            case RESTART:
                state = Status.STARTED;
                doBegin();
                break;
            case STOP:
            case CONTINUE:
                state = Status.STOPPED;
                termination.complete(null);
                break;
            default:
                throw new IllegalArgumentException("Unexpected transition: " + state + " -" + whatToDo + "-> ???");
        }
    }

    private void doBegin() {
        if (getEnvironment() == null) {
            throw new IllegalStateException();
        }
        ensureCurrentStateIs(Status.STARTED);
        getEngine().submit(this::begin);
    }

    private void begin() {
        nextOperation = Continuation.CONTINUE;
        try {
            onBegin();
        } catch (Exception e) {
            nextOperation = onError(e);
        } finally {
            doStateTransition(nextOperation);
        }
    }

    public void onBegin() throws Exception {
        // does nothing by default
    }

    private void doRun() {
        ensureCurrentStateIs(EnumSet.of(Status.PAUSED, Status.RUNNING));
        getEngine().submit(this::run);
    }

    private void run() {
        nextOperation = Continuation.CONTINUE;
        try {
            onRun();
        } catch (Exception e) {
            nextOperation = onError(e);
        } finally {
            doStateTransition(nextOperation);
        }
    }

    public abstract void onRun() throws Exception;

    private void doEnd() {
        getEngine().submit(this::end);
    }

    private void end() {
        nextOperation = Continuation.CONTINUE;
        try {
            onEnd();
        } catch (Exception e) {
            nextOperation = onError(e);
        } finally {
            doStateTransition(nextOperation);
        }
    }

    public void onEnd() throws Exception {
        // does nothing by default
    }

    private Continuation onError(Exception e) {
        return onUncaughtError(e);
    }

    public Continuation onUncaughtError(Exception e) {
        e.printStackTrace();
        return Continuation.STOP;
    }

    public final void start() {
        ensureCurrentStateIs(Status.CREATED);
        doStateTransition(Continuation.CONTINUE);
    }

    protected final void resume() {
        ensureCurrentStateIs(Status.PAUSED);
        doStateTransition(Continuation.CONTINUE);
    }

    protected final void resumeIfPaused() {
        if (currentStateIs(Status.PAUSED)) {
            doStateTransition(Continuation.CONTINUE);
        }
    }

    protected final void pause() {
        ensureCurrentStateIs(EnumSet.of(Status.STARTED, Status.RUNNING, Status.PAUSED));
        nextOperation = Continuation.PAUSE;
    }

    protected final void stop() {
        ensureCurrentStateIs(EnumSet.of(Status.STARTED, Status.RUNNING, Status.PAUSED));
        if (currentStateIs(Status.PAUSED)) {
            doStateTransition(Continuation.STOP);
        } else {
            nextOperation = Continuation.STOP;
        }
    }

    protected final void restart() {
        ensureCurrentStateIs(EnumSet.complementOf(EnumSet.of(Status.CREATED)));
        nextOperation = Continuation.RESTART;
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected final void setEnvironment(Environment environment) {
        this.environment = Objects.requireNonNull(environment);
        id = new AgentId(id.getLocalName(), environment.getName());
    }

    protected ExecutorService getEngine() {
        return getEnvironment() != null ? getEnvironment().getEngine() : null;
    }

    protected final void log(Object format, Object... args) {
        System.out.printf("[" + getAgentId() + "] " + format + "\n", args);
    }

    public AgentId getAgentId() {
        return id;
    }

    public void await(Duration duration) throws InterruptedException, ExecutionException, TimeoutException {
        termination.get(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void await() throws InterruptedException, ExecutionException, TimeoutException {
        termination.get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    protected enum Status {
        CREATED, STARTED, RUNNING, PAUSED, STOPPED
    }

}
