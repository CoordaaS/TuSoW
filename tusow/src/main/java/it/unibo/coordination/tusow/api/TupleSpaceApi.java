package it.unibo.coordination.tusow.api;

import io.vertx.core.Promise;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;

import java.util.Collection;

public interface TupleSpaceApi<T extends Tuple<T>, TT extends Template<T>, K, V, M extends Match<T, TT, K, V>> extends Api {

    void createNewTuples(String tupleSpaceName, boolean bulk, Collection<? extends T> tuples, Promise<Collection<? extends T>> promise);

    void observeTuples(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, TT template, Promise<Collection<? extends M>> promise);

    void consumeTuples(String tupleSpaceName, boolean bulk, boolean predicative, TT template, Promise<Collection<? extends M>> promise);

    void getAllTuples(String tupleSpaceName, Promise<Collection<? extends T>> promise);

    void countTuples(String tupleSpaceName, Promise<Integer> promise);

}

