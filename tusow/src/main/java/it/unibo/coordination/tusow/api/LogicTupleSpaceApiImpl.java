package it.unibo.coordination.tusow.api;

import alice.tuprolog.Term;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.NotImplementedError;
import it.unibo.coordination.tusow.linda.TupleSpaces;
import it.unibo.coordination.tusow.presentation.*;

import java.util.List;

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
    public void observeTuples(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, LogicTemplateRepresentation template, Handler<AsyncResult<? super ListRepresentation<? extends MatchRepresentation<LogicTupleRepresentation, LogicTemplateRepresentation, String, Term>>>> handler) {
        final var logicSpace = TupleSpaces.getLogicSpace(tupleSpaceName);

        if (bulk && predicative) throw new BadContentError();

        if (negated) {
            if (bulk) {
                throw new NotImplementedError();
            } else if (predicative) {
                logicSpace.tryAbsent(template).thenAcceptAsync(t -> {
                    ListOfLogicMatchRepresentation result = new ListOfLogicMatchRepresentation(
                        LogicMatchRepresentation.wrap(t)
                    );
                    handler.handle(Future.succeededFuture(result));
                });
            } else {
                logicSpace.absent(template).thenAcceptAsync(t -> {
                    var lots = new ListOfLogicTupleRepresentation(List.of());
                    handler.handle(Future.succeededFuture(lots));
                });
            }
        } else {
            if (bulk) {
                logicSpace.readAll(template).thenAcceptAsync(ts -> {
                    var lots = new ListOfLogicTupleRepresentation(ts.stream().map(LogicTupleRepresentation::wrap));
                    handler.handle(Future.succeededFuture(lots));
                });
            } else if (predicative) {
                logicSpace.tryRead(template).thenAcceptAsync(ts -> {
                    var lots = new ListOfLogicTupleRepresentation(ts.stream().map(LogicTupleRepresentation::wrap));
                    handler.handle(Future.succeededFuture(lots));
                });
            } else {
                logicSpace.read(template).thenAcceptAsync(t -> {
                    var lots = new ListOfLogicTupleRepresentation(List.of(LogicTupleRepresentation.wrap(t)));
                    handler.handle(Future.succeededFuture(lots));
                });
            }
        }
    }

    @Override
    public void consumeTuples(String tupleSpaceName, boolean bulk, boolean predicative, LogicTemplateRepresentation template, Handler<AsyncResult<? super ListRepresentation<? extends MatchRepresentation<LogicTupleRepresentation, LogicTemplateRepresentation, String, Term>>>> handler) {

        final var logicSpace = TupleSpaces.getLogicSpace(tupleSpaceName);

        if (bulk && predicative) throw new BadContentError();

        if (bulk) {
            logicSpace.takeAll(template).thenAcceptAsync(ts -> {
                var lots = new ListOfLogicTupleRepresentation(ts.stream().map(LogicTupleRepresentation::wrap));
                handler.handle(Future.succeededFuture(lots));
            });
        } else if (predicative) {
            logicSpace.tryTake(template).thenAcceptAsync(ts -> {
                var lots = new ListOfLogicTupleRepresentation(ts.stream().map(LogicTupleRepresentation::wrap));
                handler.handle(Future.succeededFuture(lots));
            });
        } else {
            logicSpace.take(template).thenAcceptAsync(t -> {
                var lots = new ListOfLogicTupleRepresentation(List.of(LogicTupleRepresentation.wrap(t)));
                handler.handle(Future.succeededFuture(lots));
            });
        }
    }


}
