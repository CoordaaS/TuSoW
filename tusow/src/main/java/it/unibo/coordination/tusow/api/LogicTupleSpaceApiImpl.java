package it.unibo.coordination.tusow.api;

import alice.tuprolog.Term;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.logic.LogicMatch;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.NotImplementedError;
import it.unibo.coordination.tusow.linda.TupleSpaces;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class LogicTupleSpaceApiImpl extends AbstractApi implements LogicTupleSpaceApi {

    LogicTupleSpaceApiImpl(RoutingContext routingContext) {
        super(routingContext);
    }

    @Override
    public void createNewTuples(String tupleSpaceName, boolean bulk, Collection<? extends LogicTuple> tuples, Handler<AsyncResult<Collection<? extends LogicTuple>>> handler) {

        final var logicSpace = TupleSpaces.getLogicSpace(tupleSpaceName);

        if (bulk) {
            logicSpace.writeAll(tuples).thenAcceptAsync(ts -> {
                handler.handle(Future.succeededFuture(ts));
            });
        } else {
            if (tuples.isEmpty()) {
                throw new BadContentError();
            }

            logicSpace.write(tuples.stream().findFirst().get()).thenAcceptAsync(t -> {
                handler.handle(Future.succeededFuture(List.of(t)));
            });
        }
    }

    @Override
    public void observeTuples(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, LogicTemplate template, Handler<AsyncResult<Collection<? extends LogicMatch>>> handler) {
        final var logicSpace = TupleSpaces.getLogicSpace(tupleSpaceName);

        if (bulk && predicative) throw new BadContentError();

        if (negated) {
            if (bulk) {
                throw new NotImplementedError();
            } else if (predicative) {
                logicSpace.tryAbsent(template).thenAcceptAsync(singleMatchHandler(handler));
            } else {
                logicSpace.absent(template).thenAcceptAsync(singleMatchHandler(handler));
            }
        } else {
            if (bulk) {
                logicSpace.readAll(template).thenAcceptAsync(ts -> {
                    handler.handle(Future.succeededFuture(
                            ts.stream().map(LogicMatch::wrap).collect(Collectors.toList())
                    ));
                });
            } else if (predicative) {
                logicSpace.tryRead(template).thenAcceptAsync(singleMatchHandler(handler));
            } else {
                logicSpace.read(template).thenAcceptAsync(singleMatchHandler(handler));
            }
        }
    }

    @Override
    public void consumeTuples(String tupleSpaceName, boolean bulk, boolean predicative, LogicTemplate template, Handler<AsyncResult<Collection<? extends LogicMatch>>> handler) {
        final var logicSpace = TupleSpaces.getLogicSpace(tupleSpaceName);

        if (bulk && predicative) throw new BadContentError();

        if (bulk) {
            logicSpace.takeAll(template).thenAcceptAsync(ts -> {
                handler.handle(Future.succeededFuture(
                        ts.stream().map(LogicMatch::wrap).collect(Collectors.toList())
                ));
            });
        } else if (predicative) {
            logicSpace.tryTake(template).thenAcceptAsync(singleMatchHandler(handler));
        } else {
            logicSpace.take(template).thenAcceptAsync(singleMatchHandler(handler));
        }
    }

    private Consumer<Match<LogicTuple, LogicTemplate, String, Term>> singleMatchHandler(Handler<AsyncResult<Collection<? extends LogicMatch>>> handler) {
        return t -> handler.handle(Future.succeededFuture(List.of(LogicMatch.wrap(t))));
    }

    @Override
    public void getAllTuples(String tupleSpaceName, Handler<AsyncResult<Collection<? extends LogicTuple>>> handler) {
        TupleSpaces.getLogicSpace(tupleSpaceName)
                .get()
                .thenAcceptAsync(ts -> handler.handle(Future.succeededFuture(ts)));

    }

    @Override
    public void countTuples(String tupleSpaceName, Handler<AsyncResult<Integer>> handler) {
        TupleSpaces.getLogicSpace(tupleSpaceName)
                .getSize()
                .thenAcceptAsync(n -> handler.handle(Future.succeededFuture(n)));
    }

}
