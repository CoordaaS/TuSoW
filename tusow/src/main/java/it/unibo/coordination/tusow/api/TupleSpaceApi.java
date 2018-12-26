package it.unibo.coordination.tusow.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import it.unibo.coordination.tusow.presentation.ListRepresentation;
import it.unibo.coordination.tusow.presentation.MatchRepresentation;
import it.unibo.coordination.tusow.presentation.TemplateRepresentation;
import it.unibo.coordination.tusow.presentation.TupleRepresentation;

public interface TupleSpaceApi<T extends TupleRepresentation,
        TT extends TemplateRepresentation, K, V,
        M extends MatchRepresentation<T, TT, K, V>,
        LT extends ListRepresentation<T>,
        LM extends ListRepresentation<M>> extends Api {

    void createNewTuples(String tupleSpaceName, boolean bulk, LT tuples, Handler<AsyncResult<LT>> handler);

    void observeTuples(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, TT template, Handler<AsyncResult<LM>> handler);

    void consumeTuples(String tupleSpaceName, boolean bulk, boolean predicative, TT template, Handler<AsyncResult<LM>> handler);

}

