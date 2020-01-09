package it.unibo.coordination.tusow.api;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import it.unibo.coordination.linda.core.TupleSpace;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.NotImplementedError;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static it.unibo.coordination.utils.CollectionUtils.listOf;

abstract class AbstractTupleSpaceApi<T extends Tuple<T>, TT extends Template<T>, K, V, M extends Match<T, TT, K, V>, TS extends TupleSpace<T, TT, K, V, M>>
        extends AbstractApi implements TupleSpaceApi<T, TT, K, V, M> {

    public AbstractTupleSpaceApi(RoutingContext routingContext) {
        super(routingContext);
    }

    protected abstract TS getTupleSpaceByName(String name);

    @Override
    public void createNewTuples(String tupleSpaceName, boolean bulk, Collection<? extends T> tuples, Promise<Collection<? extends T>> promise) {

        final TS tupleSpace = getTupleSpaceByName(tupleSpaceName);

        if (bulk) {
            tupleSpace.writeAll(tuples).thenAcceptAsync(promise::complete);
        } else {
            if (tuples.isEmpty()) {
                throw new BadContentError();
            }

            tupleSpace.write(tuples.stream().findFirst().get()).thenAcceptAsync(t -> {
                promise.complete(listOf(t));
            });
        }
    }

    @Override
    public void observeTuples(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, TT template, Promise<Collection<? extends M>> promise) {
        final TS tupleSpace = getTupleSpaceByName(tupleSpaceName);

        if (bulk && predicative) throw new BadContentError();

        if (negated) {
            if (bulk) {
                throw new NotImplementedError();
            } else if (predicative) {
                tupleSpace.tryAbsent(template).thenAcceptAsync(singleMatchHandler(promise));
            } else {
                tupleSpace.absent(template).thenAcceptAsync(singleMatchHandler(promise));
            }
        } else {
            if (bulk) {
                tupleSpace.readAll(template).thenAcceptAsync(ts -> {
                    promise.complete(
                            ts.stream().map(this::ensureCorrectTypeForMatch).collect(Collectors.toList())
                    );
                });
            } else if (predicative) {
                tupleSpace.tryRead(template).thenAcceptAsync(singleMatchHandler(promise));
            } else {
                tupleSpace.read(template).thenAcceptAsync(singleMatchHandler(promise));
            }
        }
    }

    @Override
    public void consumeTuples(String tupleSpaceName, boolean bulk, boolean predicative, TT template, Promise<Collection<? extends M>> promise) {
        final TS tupleSpace = getTupleSpaceByName(tupleSpaceName);

        if (bulk && predicative) throw new BadContentError();

        if (bulk) {
            tupleSpace.takeAll(template).thenAcceptAsync(ts -> {
                promise.complete(
                        ts.stream().map(this::ensureCorrectTypeForMatch).collect(Collectors.toList())
                );
            });
        } else if (predicative) {
            tupleSpace.tryTake(template).thenAcceptAsync(singleMatchHandler(promise));
        } else {
            tupleSpace.take(template).thenAcceptAsync(singleMatchHandler(promise));
        }
    }

    protected abstract M ensureCorrectTypeForMatch(Match<T, TT, K, V> match);

    private Consumer<Match<T, TT, K, V>> singleMatchHandler(Promise<Collection<? extends M>> promise) {
        return t -> promise.complete(listOf(ensureCorrectTypeForMatch(t)));
    }

    @Override
    public void getAllTuples(String tupleSpaceName, Promise<Collection<? extends T>> promise) {
        getTupleSpaceByName(tupleSpaceName)
                .get()
                .thenAcceptAsync(promise::complete);

    }

    @Override
    public void countTuples(String tupleSpaceName, Promise<Integer> promise) {
        getTupleSpaceByName(tupleSpaceName)
                .getSize()
                .thenAcceptAsync(promise::complete);
    }

}
