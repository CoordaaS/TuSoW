package it.unibo.coordination.linda.logic;

import it.unibo.coordination.utils.CollectionUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class NonDeterminismLogicSpaceImpl extends AbstractLogicSpaceImpl implements InspectableLogicSpace {

    private final List<PendingRequest> pendingQueue = new LinkedList<>();

    public NonDeterminismLogicSpaceImpl(String name, ExecutorService executor) {
        super(name, executor);
    }

    @Override
    protected Collection<PendingRequest> getPendingRequests() {
        return pendingQueue;
    }

    @Override
    protected Iterator<PendingRequest> getPendingRequestsIterator() {
        return CollectionUtils.randomIterator(pendingQueue);
    }

    @Override
    protected Stream<LogicTuple> lookForTuples(LogicTemplate template, int limit) {
        final var buffered = lookForTuplesImpl(template, Integer.MAX_VALUE).collect(Collectors.toList());
        Collections.sort(buffered);
        return buffered.stream().limit(limit);
    }

    @Override
    protected Stream<LogicTuple> retrieveTuples(LogicTemplate template, int limit) {
        final var buffered = retrieveTuplesImpl(template, Integer.MAX_VALUE).collect(Collectors.toList());
        Collections.sort(buffered);
        return buffered.stream().limit(limit);
    }
}
