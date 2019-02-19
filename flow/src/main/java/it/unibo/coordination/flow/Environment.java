package it.unibo.coordination.flow;

import it.unibo.sd1819.lab10.ts.logic.LogicTupleSpace;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class Environment {
    private final ExecutorService engine;
    private final String name;
    private final List<BaseAgent> agents = new LinkedList<>();

    public Environment(ExecutorService engine, String name) {
        this.engine = Objects.requireNonNull(engine);
        this.name = Optional.ofNullable(name).orElseGet(() -> getClass().getSimpleName() + "_" + System.identityHashCode(this));
    }

    public Environment(String name) {
        this(Executors.newCachedThreadPool(), name);
    }

    public Environment(ExecutorService engine) {
        this(engine, null);
    }

    public Environment() {
        this((String) null);
    }

    public abstract LogicTupleSpace getTupleSpace(String name);

    public String getWhitePagesTupleSpaceName() {
        return "white-pages@" + getName();
    }

    public LogicTupleSpace getWhitePagesTupleSpace() {
        return getTupleSpace(getWhitePagesTupleSpaceName());
    }

    public String getBlackboardTupleSpaceName() {
        return "blackboard@" + getName();
    }

    public LogicTupleSpace getBlackboardTupleSpace() {
        return getTupleSpace(getBlackboardTupleSpaceName());
    }

    public <A extends BaseAgent> A createAgent(Class<A> agentClass, String name, boolean andStart, Object... args) {

        final Object[] arguments = Stream.concat(Stream.of(generateAgentId(name)), Stream.of(args)).toArray();

        final Optional<A> newAgent = Stream.of(agentClass.getConstructors())
                .filter(c -> c.getParameterCount() == arguments.length)
                .map(constructor -> {
                    try {
                        final A agent = (A) constructor.newInstance(arguments);
                        return Optional.of(agent);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        return Optional.<A>empty();
                    }
                }).filter(Optional::isPresent)
                .findAny()
                .flatMap(Function.identity());

        if (!newAgent.isPresent()) {
            throw new IllegalArgumentException("No constructor for class " + agentClass.getName() + " accepts arguments: " + Arrays.toString(arguments));
        }

        return registerAgent(newAgent.get(), andStart);

    }

    public <A extends BaseAgent> A registerAgent(A agent, boolean andStart) {
        agent.setEnvironment(this);
        agents.add(agent);
        if (andStart) {
            agent.start();
        }
        return agent;
    }

    public List<BaseAgent> getAgents() {
        return new ArrayList<>(agents);
    }

    public String getName() {
        return name;
    }

    public Environment awaitAllAgentsStop(Duration duration) throws InterruptedException, ExecutionException, TimeoutException {
        for (BaseAgent a : getAgents()) {
            a.await(duration);
        }
        return this;
    }

    public Environment shutdown() {
        getEngine().shutdown();
        return this;
    }

    public Environment awaitShutdown(Duration duration) throws InterruptedException {
        getEngine().awaitTermination(duration.toMillis(), TimeUnit.MILLISECONDS);
        return this;
    }

    public AgentId generateAgentId(String raw) {
        if (raw.contains("@")) {
            return AgentId.parse(raw);
        } else {
            return new AgentId(raw, getName());
        }
    }

    public ExecutorService getEngine() {
        return engine;
    }
}
