package it.unibo.coordination.tusow.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.tusow.presentation.*;

import java.util.Collection;
import java.util.function.Function;

public interface TupleSpaceApi<T extends TupleRepresentation, TT extends TemplateRepresentation> extends Api {

//    void createNewTuple(String tupleSpaceName,
//               T tuple,
//               Handler<AsyncResult<T>> handler);

    void createNewTuples(String tupleSpaceName,
                        ListRepresentation<T> tuples,
                        Handler<AsyncResult<? super ListRepresentation<T>>> handler);

//    void observeTuple(String tupleSpaceName,
//                      boolean predicative,
//                      TT template,
//                      Handler<AsyncResult<T>> handler);

    void observeTuples(String tupleSpaceName,
                      boolean predicative,
                      ListRepresentation<TT> template,
                      Handler<AsyncResult<ListRepresentation<T>>> handler);

//    void consumeTuple(String tupleSpaceName,
//                       boolean predicative,
//                       TT template,
//                       Handler<AsyncResult<T>> handler);

    void consumeTuples(String tupleSpaceName,
                      boolean predicative,
                      ListRepresentation<TT> template,
                      Handler<AsyncResult<ListRepresentation<T>>> handler);

//    void absentTuple(String tupleSpaceName,
//                      boolean predicative,
//                      TT template,
//                      Handler<AsyncResult<T>> handler);

    void absentTuples(String tupleSpaceName,
                       boolean predicative,
                       ListRepresentation<TT> template,
                       Handler<AsyncResult<ListRepresentation<T>>> handler);
}

