package it.unibo.coordination.tuples.logic;

import it.unibo.tuprolog.utils.CollectionUtils;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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
    protected MultiSet<LogicTuple> lookForTuples(LogicTemplate template, int limit) {
        return lookForTuplesImpl(template, limit).collect(Collectors.toCollection(HashMultiSet::new));
    }

    @Override
    protected MultiSet<LogicTuple> retrieveTuples(LogicTemplate template, int limit) {
        return retrieveTuplesImpl(template, limit).collect(Collectors.toCollection(HashMultiSet::new));
    }
}
