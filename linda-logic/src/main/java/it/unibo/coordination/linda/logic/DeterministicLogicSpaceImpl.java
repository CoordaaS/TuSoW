package it.unibo.coordination.linda.logic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

class DeterministicLogicSpaceImpl extends AbstractLogicSpaceImpl implements InspectableLogicSpace {

    private final List<PendingRequest> pendingQueue = new LinkedList<>();

    public DeterministicLogicSpaceImpl(String name, ExecutorService executor) {
        super(name, executor);
    }

    @Override
    protected Collection<PendingRequest> getPendingRequests() {
        return pendingQueue;
    }

    @Override
    protected Stream<LogicTuple> lookForTuples(LogicTemplate template, int limit) {
        return lookForTuplesImpl(template, limit);
    }

    @Override
    protected Stream<LogicTuple> retrieveTuples(LogicTemplate template, int limit) {
        return retrieveTuplesImpl(template, limit);
    }
}
