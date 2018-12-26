package it.unibo.coordination.tusow.routes;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.tusow.api.TupleSpaceApi;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.HttpError;
import it.unibo.coordination.tusow.exceptions.InternalServerError;
import it.unibo.coordination.utils.Tuple;
import it.unibo.coordination.utils.Tuple3;
import it.unibo.coordination.utils.Tuple4;
import it.unibo.coordination.utils.Tuple5;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static it.unibo.coordination.tusow.presentation.MIMETypes.APPLICATION_JSON;
import static it.unibo.coordination.tusow.presentation.MIMETypes.APPLICATION_YAML;

public abstract class AbstractTupleSpacePath<T extends TupleRepresentation, TT extends TemplateRepresentation, K, V> extends Path {

    public AbstractTupleSpacePath(String tupleSpaceType) {
        super("/" + Objects.requireNonNull(tupleSpaceType) + "/:tupleSpaceName");
    }

    @Override
    protected void setupRoutes() {
        addRoute(HttpMethod.DELETE, this::delete)
                .consumes(APPLICATION_JSON)
//                .consumes(APPLICATION_XML)
                .consumes(APPLICATION_YAML)
                .produces(APPLICATION_JSON)
//                .produces(APPLICATION_XML)
                .produces(APPLICATION_YAML);

        addRoute(HttpMethod.GET, this::get)
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

    protected abstract TupleSpaceApi<T, TT, K, V> getTupleSpaceApi(RoutingContext routingContext);

    public void post(RoutingContext routingContext) {
        final var api = getTupleSpaceApi(routingContext);
        final Future<ListRepresentation<T>> result = Future.future();

        try {
            final String tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final Optional<Boolean> bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final ListRepresentation<T> tuples = parseTuples(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());

            final Tuple3<String, Boolean, ListRepresentation<T>> cleanInputs = validateInputsForPost(tupleSpaceName, bulk, tuples);

            result.setHandler(responseHandler(routingContext, response -> validateOutputsForPost(cleanInputs, response)));

            api.createNewTuples(cleanInputs.getFirst(), cleanInputs.getSecond(), cleanInputs.getThird(), result);
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    protected abstract ListRepresentation<T> parseTuples(String mimeType, String payload) throws IOException;

    private Tuple3<String, Boolean, ListRepresentation<T>> validateInputsForPost(String tupleSpaceName, Optional<Boolean> bulk, ListRepresentation<T> tuples) {
        final var bulkValue = bulk.orElse(false);

        if (!bulkValue && tuples.getItems().size() != 1) {
            throw new BadContentError();
        } else if (tuples.getItems().size() == 0) {
            throw new BadContentError();
        }

        return Tuple.of(
                Objects.requireNonNull(tupleSpaceName),
                bulkValue,
                tuples
            );
    }

    private ListRepresentation<T> validateOutputsForPost(Tuple3<String, Boolean, ListRepresentation<T>> inputs, ListRepresentation<T> output) {
        return validateOutputsForPost(inputs.getFirst(), inputs.getSecond(), inputs.getThird(), output);
    }

    private ListRepresentation<T> validateOutputsForPost(String tupleSpaceName, boolean bulk, ListRepresentation<T> input, ListRepresentation<T> output) {
        if (input.getItems().size() != output.getItems().size()) {
            throw new InternalServerError();
        }

        return output;
    }

    public void delete(RoutingContext routingContext) {
        final var api = getTupleSpaceApi(routingContext);
        final Future<? super ListRepresentation<? extends MatchRepresentation<T, TT, K, V>>> result = Future.future();

        try {
            final String tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final Optional<Boolean> bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final Optional<Boolean> predicative = Optional.ofNullable(routingContext.queryParams().get("predicative")).map(Boolean::parseBoolean);
            final TT template = parseTemplate(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());

            final Tuple4<String, Boolean, Boolean, TT> cleanInputs = validateInputsForDelete(tupleSpaceName, bulk, predicative, template);

            result.setHandler(responseHandler(routingContext, response -> validateOutputsForDelete(cleanInputs, response)));

            api.consumeTuples(cleanInputs.getFirst(), cleanInputs.getSecond(), cleanInputs.getThird(), cleanInputs.getFourth(), result.completer());
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    protected abstract TT parseTemplate(String mimeType, String payload) throws IOException;

    private Tuple4<String, Boolean, Boolean, TT> validateInputsForDelete(String tupleSpaceName, Optional<Boolean> bulk, Optional<Boolean> predicative, TT template) {
        return Tuple.of(
                Objects.requireNonNull(tupleSpaceName),
                bulk.orElse(false),
                predicative.orElse(false),
                template
            );
    }

    private <TL extends ListRepresentation<T>> TL validateOutputsForDelete(Tuple4<String, Boolean, Boolean, TT> inputs, TL output) {
        return validateOutputsForDelete(inputs.getFirst(), inputs.getSecond(), inputs.getThird(), inputs.getFourth(), output);
    }

    private <TL extends ListRepresentation<T>> TL validateOutputsForDelete(String tupleSpaceName, boolean bulk, boolean predicative, TT template, TL output) {

        if (!bulk && output.getItems().size() > 1) {
            throw new InternalServerError();
        }

        return output;
    }

    public void get(RoutingContext routingContext) {
        final var api = getTupleSpaceApi(routingContext);
        final Future<ListRepresentation<T>> result = Future.future();

        try {
            final var tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final var bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final var predicative = Optional.ofNullable(routingContext.queryParams().get("predicative")).map(Boolean::parseBoolean);
            final var negated = Optional.ofNullable(routingContext.queryParams().get("negated")).map(Boolean::parseBoolean);
            final var template = parseTemplate(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());

            final var cleanInputs = validateInputsForGet(tupleSpaceName, bulk, predicative, negated, template);

            result.setHandler(responseHandler(routingContext, response -> validateOutputsForGet(cleanInputs, response)));

            api.observeTuples(cleanInputs.getFirst(), cleanInputs.getSecond(), cleanInputs.getThird(), cleanInputs.getFourth(), cleanInputs.getFifth(), result);
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private Tuple5<String, Boolean, Boolean, Boolean, TT> validateInputsForGet(String tupleSpaceName, Optional<Boolean> bulk, Optional<Boolean> predicative, Optional<Boolean> negated, TT template) {
        return Tuple.of(
                Objects.requireNonNull(tupleSpaceName),
                bulk.orElse(false),
                predicative.orElse(false),
                negated.orElse(false),
                template
        );
    }

    private <TL extends ListRepresentation<T>> TL validateOutputsForGet(Tuple5<String, Boolean, Boolean, Boolean, TT> inputs, TL output) {
        return validateOutputsForDelete(inputs.getFirst(), inputs.getSecond(), inputs.getThird(), inputs.getFourth(), inputs.getFifth(), output);
    }

    private <TL extends ListRepresentation<T>> TL validateOutputsForDelete(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, TT template, TL output) {

        if (!bulk && output.getItems().size() > 1) {
            throw new InternalServerError();
        }

        return output;
    }

}