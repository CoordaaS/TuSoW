package it.unibo.coordination.tusow.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Collection;

public interface TupleSpaceApi<T extends Tuple, TT extends Template, K, V, M extends Match<T, TT, K, V>> extends Api {

    void createNewTuples(String tupleSpaceName, boolean bulk, Collection<? extends T> tuples, Handler<AsyncResult<Collection<? extends T>>> handler);

    void observeTuples(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, TT template, Handler<AsyncResult<Collection<? extends M>>> handler);

    void consumeTuples(String tupleSpaceName, boolean bulk, boolean predicative, TT template, Handler<AsyncResult<Collection<? extends M>>> handler);

    void getAllTuples(String tupleSpaceName, Handler<AsyncResult<Collection<? extends T>>> handler);

    void countTuples(String tupleSpaceName, Handler<AsyncResult<Integer>> handler);

}

