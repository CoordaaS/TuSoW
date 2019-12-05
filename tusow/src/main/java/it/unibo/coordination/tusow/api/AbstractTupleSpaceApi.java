package it.unibo.coordination.tusow.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.linda.core.TupleSpace;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.NotImplementedError;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

abstract class AbstractTupleSpaceApi<T extends Tuple<T>, TT extends Template<T>, K, V, M extends Match<T, TT, K, V>, TS extends TupleSpace<T, TT, K, V, M>>
        extends AbstractApi implements TupleSpaceApi<T, TT, K, V, M> {

    public AbstractTupleSpaceApi(RoutingContext routingContext) {
        super(routingContext);
    }

    protected abstract TS getTupleSpaceByName(String name);

    @Override
    public void createNewTuples(String tupleSpaceName, boolean bulk, Collection<? extends T> tuples, Handler<AsyncResult<Collection<? extends T>>> handler) {

        final TS tupleSpace = getTupleSpaceByName(tupleSpaceName);

        if (bulk) {
            tupleSpace.writeAll(tuples).thenAcceptAsync(ts -> {
                handler.handle(Future.succeededFuture(ts));
            });
        } else {
            if (tuples.isEmpty()) {
                throw new BadContentError();
            }

            tupleSpace.write(tuples.stream().findFirst().get()).thenAcceptAsync(t -> {
                handler.handle(Future.succeededFuture(List.of(t)));
            });
        }
    }

    @Override
    public void observeTuples(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, TT template, Handler<AsyncResult<Collection<? extends M>>> handler) {
        final TS tupleSpace = getTupleSpaceByName(tupleSpaceName);

        if (bulk && predicative) throw new BadContentError();

        if (negated) {
            if (bulk) {
                throw new NotImplementedError();
            } else if (predicative) {
                tupleSpace.tryAbsent(template).thenAcceptAsync(singleMatchHandler(handler));
            } else {
                tupleSpace.absent(template).thenAcceptAsync(singleMatchHandler(handler));
            }
        } else {
            if (bulk) {
                tupleSpace.readAll(template).thenAcceptAsync(ts -> {
                    handler.handle(Future.succeededFuture(
                            ts.stream().map(this::ensureCorrectTypeForMatch).collect(Collectors.toList())
                    ));
                });
            } else if (predicative) {
                tupleSpace.tryRead(template).thenAcceptAsync(singleMatchHandler(handler));
            } else {
                tupleSpace.read(template).thenAcceptAsync(singleMatchHandler(handler));
            }
        }
    }

    @Override
    public void consumeTuples(String tupleSpaceName, boolean bulk, boolean predicative, TT template, Handler<AsyncResult<Collection<? extends M>>> handler) {
        final TS tupleSpace = getTupleSpaceByName(tupleSpaceName);

        if (bulk && predicative) throw new BadContentError();

        if (bulk) {
            tupleSpace.takeAll(template).thenAcceptAsync(ts -> {
                handler.handle(Future.succeededFuture(
                        ts.stream().map(this::ensureCorrectTypeForMatch).collect(Collectors.toList())
                ));
            });
        } else if (predicative) {
            tupleSpace.tryTake(template).thenAcceptAsync(singleMatchHandler(handler));
        } else {
            tupleSpace.take(template).thenAcceptAsync(singleMatchHandler(handler));
        }
    }

    protected abstract M ensureCorrectTypeForMatch(Match<T, TT, K, V> match);

    private Consumer<Match<T, TT, K, V>> singleMatchHandler(Handler<AsyncResult<Collection<? extends M>>> handler) {
        return t -> handler.handle(Future.succeededFuture(List.of(ensureCorrectTypeForMatch(t))));
    }

    @Override
    public void getAllTuples(String tupleSpaceName, Handler<AsyncResult<Collection<? extends T>>> handler) {
        getTupleSpaceByName(tupleSpaceName)
                .get()
                .thenAcceptAsync(ts -> handler.handle(Future.succeededFuture(ts)));

    }

    @Override
    public void countTuples(String tupleSpaceName, Handler<AsyncResult<Integer>> handler) {
        getTupleSpaceByName(tupleSpaceName)
                .getSize()
                .thenAcceptAsync(n -> handler.handle(Future.succeededFuture(n)));
    }

}
