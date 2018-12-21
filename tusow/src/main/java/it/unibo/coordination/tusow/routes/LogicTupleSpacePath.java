package it.unibo.coordination.tusow.routes;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.api.LogicTupleSpaceApi;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.HttpError;
import it.unibo.coordination.tusow.exceptions.InternalServerError;
import it.unibo.coordination.tusow.presentation.ListOfLogicTupleRepresentation;
import it.unibo.coordination.tusow.presentation.LogicTemplateRepresentation;
import it.unibo.coordination.utils.Tuple;
import it.unibo.coordination.utils.Tuple3;
import it.unibo.coordination.utils.Tuple4;
import it.unibo.coordination.utils.Tuple5;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static it.unibo.coordination.tusow.presentation.MIMETypes.APPLICATION_JSON;
import static it.unibo.coordination.tusow.presentation.MIMETypes.APPLICATION_YAML;

public class LogicTupleSpacePath extends Path {


    public LogicTupleSpacePath() {
        super("/logic/{tupleSpaceName}");
    }

    @Override
    protected void setupRoutes() {
        addRoute(HttpMethod.GET, this::delete)
                .consumes(APPLICATION_JSON)
//                .consumes(APPLICATION_XML)
                .consumes(APPLICATION_YAML)
                .produces(APPLICATION_JSON)
//                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);

        addRoute(HttpMethod.POST, this::post)
                .consumes(APPLICATION_JSON)
//                .consumes(APPLICATION_XML)
                .consumes(APPLICATION_YAML)
                .produces(APPLICATION_JSON)
//                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);
    }

    public void post(RoutingContext routingContext) {
        final LogicTupleSpaceApi api = LogicTupleSpaceApi.get(routingContext);
        final Future<ListOfLogicTupleRepresentation> result = Future.future();

        try {
            final var tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final var bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final var tuples = ListOfLogicTupleRepresentation.parse(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());

            final var cleanInputs = validateInputsForPost(tupleSpaceName, bulk, tuples);

            result.setHandler(responseHandler(routingContext, response -> validateOutputsForPost(cleanInputs, response)));

            api.createNewTuples(cleanInputs.getFirst(), cleanInputs.getSecond(), cleanInputs.getThird(), result);
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private Tuple3<String, Boolean, ListOfLogicTupleRepresentation> validateInputsForPost(String tupleSpaceName, Optional<Boolean> bulk, ListOfLogicTupleRepresentation tuples) {
        final var bulkValue = bulk.orElse(false);

        if (!bulkValue && tuples.getTuples().size() != 1) {
            throw new BadContentError();
        } else if (tuples.getTuples().size() == 0) {
            throw new BadContentError();
        }

        return Tuple.of(
                Objects.requireNonNull(tupleSpaceName),
                bulkValue,
                tuples
            );
    }

    private ListOfLogicTupleRepresentation validateOutputsForPost(Tuple3<String, Boolean, ListOfLogicTupleRepresentation> inputs, ListOfLogicTupleRepresentation output) {
        return validateOutputsForPost(inputs.getFirst(), inputs.getSecond(), inputs.getThird(), output);
    }

    private ListOfLogicTupleRepresentation validateOutputsForPost(String tupleSpaceName, boolean bulk, ListOfLogicTupleRepresentation input, ListOfLogicTupleRepresentation output) {
        if (input.getTuples().size() != output.getTuples().size()) {
            throw new InternalServerError();
        }

        return output;
    }

    public void delete(RoutingContext routingContext) {
        final LogicTupleSpaceApi api = LogicTupleSpaceApi.get(routingContext);
        final Future<ListOfLogicTupleRepresentation> result = Future.future();

        try {
            final var tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final var bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final var predicative = Optional.ofNullable(routingContext.queryParams().get("predicative")).map(Boolean::parseBoolean);
            final var template = LogicTemplateRepresentation.parse(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());

            final var cleanInputs = validateInputsForDelete(tupleSpaceName, bulk, predicative, template);

            result.setHandler(responseHandler(routingContext, response -> validateOutputsForDelete(cleanInputs, response)));

            api.consumeTuples(cleanInputs.getFirst(), cleanInputs.getSecond(), cleanInputs.getThird(), cleanInputs.getFourth(), result);
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private Tuple4<String, Boolean, Boolean, LogicTemplateRepresentation> validateInputsForDelete(String tupleSpaceName, Optional<Boolean> bulk, Optional<Boolean> predicative, LogicTemplateRepresentation template) {
        return Tuple.of(
                Objects.requireNonNull(tupleSpaceName),
                bulk.orElse(false),
                predicative.orElse(false),
                template
            );
    }

    private ListOfLogicTupleRepresentation validateOutputsForDelete(Tuple4<String, Boolean, Boolean, LogicTemplateRepresentation> inputs, ListOfLogicTupleRepresentation output) {
        return validateOutputsForDelete(inputs.getFirst(), inputs.getSecond(), inputs.getThird(), inputs.getFourth(), output);
    }

    private ListOfLogicTupleRepresentation validateOutputsForDelete(String tupleSpaceName, boolean bulk, boolean predicative, LogicTemplateRepresentation template, ListOfLogicTupleRepresentation output) {

        if (!bulk && output.getTuples().size() > 1) {
            throw new InternalServerError();
        }

        return output;
    }

    public void get(RoutingContext routingContext) {
        final LogicTupleSpaceApi api = LogicTupleSpaceApi.get(routingContext);
        final Future<ListOfLogicTupleRepresentation> result = Future.future();

        try {
            final var tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final var bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final var predicative = Optional.ofNullable(routingContext.queryParams().get("predicative")).map(Boolean::parseBoolean);
            final var negated = Optional.ofNullable(routingContext.queryParams().get("negated")).map(Boolean::parseBoolean);
            final var template = LogicTemplateRepresentation.parse(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());

            final var cleanInputs = validateInputsForGet(tupleSpaceName, bulk, predicative, negated, template);

            result.setHandler(responseHandler(routingContext, response -> validateOutputsForGet(cleanInputs, response)));

            api.observeTuples(cleanInputs.getFirst(), cleanInputs.getSecond(), cleanInputs.getThird(), cleanInputs.getFourth(), cleanInputs.getFifth(), result);
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private Tuple5<String, Boolean, Boolean, Boolean, LogicTemplateRepresentation> validateInputsForGet(String tupleSpaceName, Optional<Boolean> bulk, Optional<Boolean> predicative, Optional<Boolean> negated, LogicTemplateRepresentation template) {
        return Tuple.of(
                Objects.requireNonNull(tupleSpaceName),
                bulk.orElse(false),
                predicative.orElse(false),
                negated.orElse(false),
                template
        );
    }

    private ListOfLogicTupleRepresentation validateOutputsForGet(Tuple5<String, Boolean, Boolean, Boolean, LogicTemplateRepresentation> inputs, ListOfLogicTupleRepresentation output) {
        return validateOutputsForDelete(inputs.getFirst(), inputs.getSecond(), inputs.getThird(), inputs.getFourth(), inputs.getFifth(), output);
    }

    private ListOfLogicTupleRepresentation validateOutputsForDelete(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, LogicTemplateRepresentation template, ListOfLogicTupleRepresentation output) {

        if (!bulk && output.getTuples().size() > 1) {
            throw new InternalServerError();
        }

        return output;
    }

}