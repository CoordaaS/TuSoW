package it.unibo.coordination.flow;

import it.unibo.sd1819.lab10.ts.logic.LogicTupleSpace;
import it.unibo.sd1819.lab10.tusow.RemoteLogicTupleSpace;

import java.util.concurrent.ExecutorService;

public class DistributedEnvironment extends Environment {

    private final String hubHost;
    private final int hubPort;
    private final String hubRoot;

    public DistributedEnvironment(ExecutorService engine, String name, String hubHost, int hubPort, String hubRoot) {
        super(engine, name);
        this.hubHost = hubHost;
        this.hubPort = hubPort;
        this.hubRoot = hubRoot;
    }

    public DistributedEnvironment(String name, String hubHost, int hubPort, String hubRoot) {
        super(name);
        this.hubHost = hubHost;
        this.hubPort = hubPort;
        this.hubRoot = hubRoot;
    }

    public DistributedEnvironment(ExecutorService engine, String hubHost, int hubPort, String hubRoot) {
        super(engine);
        this.hubHost = hubHost;
        this.hubPort = hubPort;
        this.hubRoot = hubRoot;
    }

    public DistributedEnvironment(String hubHost, int hubPort, String hubRoot) {
        this.hubHost = hubHost;
        this.hubPort = hubPort;
        this.hubRoot = hubRoot;
    }

    @Override
    public LogicTupleSpace getTupleSpace(String name) {
        return new RemoteLogicTupleSpace(hubHost, hubPort, hubRoot, name);
    }


}
