package it.unibo.coordination.tusow.api;

import alice.tuprolog.Term;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.logic.LogicTemplate;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.NotImplementedError;
import it.unibo.coordination.tusow.linda.TupleSpaces;
import it.unibo.coordination.tusow.presentation.*;

import java.util.List;
import java.util.function.Consumer;

class LogicTupleSpaceApiImpl extends AbstractApi implements LogicTupleSpaceApi {

    LogicTupleSpaceApiImpl(RoutingContext routingContext) {
        super(routingContext);
    }

    @Override
    public void createNewTuples(String tupleSpaceName, boolean bulk, ListRepresentation<LogicTupleRepresentation> tuples, Handler<AsyncResult<ListRepresentation<LogicTupleRepresentation>>> handler) {

        final var logicSpace = TupleSpaces.getLogicSpace(tupleSpaceName);

        if (bulk) {
            logicSpace.writeAll(tuples.getItems()).thenAcceptAsync(ts -> {
                var lots = new ListOfLogicTupleRepresentation(ts.stream().map(LogicTupleRepresentation::wrap));
                handler.handle(Future.succeededFuture(lots));
            });
        } else {
            logicSpace.write(tuples.getItems().get(0)).thenAcceptAsync(t -> {
                var lots = new ListOfLogicTupleRepresentation(List.of(LogicTupleRepresentation.wrap(t)));
                handler.handle(Future.succeededFuture(lots));
            });
        }
    }

    @Override
    public void observeTuples(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, LogicTemplateRepresentation template, Handler<AsyncResult<ListRepresentation<? extends MatchRepresentation<LogicTupleRepresentation, LogicTemplateRepresentation, String, Term>>>> handler) {
        final var logicSpace = TupleSpaces.getLogicSpace(tupleSpaceName);

        final Consumer<Match<LogicTuple, LogicTemplate, String, Term>> singleMatchHandler = t -> {
            final ListRepresentation<MatchRepresentation<LogicTupleRepresentation, LogicTemplateRepresentation, String, Term>> result = new ListOfLogicMatchRepresentation(
                    LogicMatchRepresentation.wrap(t)
            );
            handler.handle(Future.succeededFuture(result));
        };

        if (bulk && predicative) throw new BadContentError();

        if (negated) {
            if (bulk) {
                throw new NotImplementedError();
            } else if (predicative) {
                logicSpace.tryAbsent(template).thenAcceptAsync(singleMatchHandler);
            } else {
                logicSpace.absent(template).thenAcceptAsync(singleMatchHandler);
            }
        } else {
            if (bulk) {
                logicSpace.readAll(template).thenAcceptAsync(ts -> {
                    var lots = new ListOfLogicMatchRepresentation(ts.stream().map(LogicMatchRepresentation::wrap));
                    handler.handle(Future.succeededFuture(lots));
                });
            } else if (predicative) {
                logicSpace.tryRead(template).thenAcceptAsync(singleMatchHandler);
            } else {
                logicSpace.read(template).thenAcceptAsync(singleMatchHandler);
            }
        }
    }

    @Override
    public void consumeTuples(String tupleSpaceName, boolean bulk, boolean predicative, LogicTemplateRepresentation template, Handler<? extends AsyncResult<? super ListRepresentation<? extends MatchRepresentation<LogicTupleRepresentation, LogicTemplateRepresentation, String, Term>>>> handler) {
        final var logicSpace = TupleSpaces.getLogicSpace(tupleSpaceName);

        final Consumer<Match<LogicTuple, LogicTemplate, String, Term>> singleMatchHandler = t -> {
            final var result = new ListOfLogicMatchRepresentation(
                    LogicMatchRepresentation.wrap(t)
            );
            handler.handle(Future.succeededFuture(result));
        };

        if (bulk && predicative) throw new BadContentError();

        if (bulk) {
            logicSpace.takeAll(template).thenAcceptAsync(ts -> {
                var lots = new ListOfLogicMatchRepresentation(ts.stream().map(LogicMatchRepresentation::wrap));
                handler.handle(Future.succeededFuture(lots));
            });
        } else if (predicative) {
            logicSpace.take(template).thenAcceptAsync(singleMatchHandler);
        } else {
            logicSpace.tryTake(template).thenAcceptAsync(singleMatchHandler);
        }
    }


}
