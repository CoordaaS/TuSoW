package it.unibo.coordination.tusow.routes;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.tusow.api.TupleSpaceApi;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.HttpError;
import it.unibo.coordination.tusow.exceptions.InternalServerError;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.jooq.lambda.tuple.Tuple5;


import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static it.unibo.coordination.tusow.presentation.MIMETypes.*;

public abstract class AbstractTupleSpacePath<T extends it.unibo.coordination.linda.core.Tuple, TT extends Template, K, V, M extends Match<T, TT, K, V>> extends Path {

    public AbstractTupleSpacePath(String tupleSpaceType) {
        super("/" + Objects.requireNonNull(tupleSpaceType) + "/:tupleSpaceName");
    }

    @Override
    protected void setupRoutes() {
        addRoute(HttpMethod.DELETE, this::delete)
                .consumes(APPLICATION_JSON.toString())
                .consumes(APPLICATION_XML.toString())
                .consumes(APPLICATION_YAML.toString())
                .produces(APPLICATION_JSON.toString())
                .produces(APPLICATION_XML.toString())
                .produces(APPLICATION_YAML.toString());

        addRoute(HttpMethod.GET, this::get)
                .consumes(APPLICATION_JSON.toString())
                .consumes(APPLICATION_XML.toString())
                .consumes(APPLICATION_YAML.toString())
                .produces(APPLICATION_JSON.toString())
                .produces(APPLICATION_XML.toString())
                .produces(APPLICATION_YAML.toString());

        addRoute(HttpMethod.POST, this::post)
                .consumes(APPLICATION_JSON.toString())
                .consumes(APPLICATION_XML.toString())
                .consumes(APPLICATION_YAML.toString())
                .produces(APPLICATION_JSON.toString())
                .produces(APPLICATION_XML.toString())
                .produces(APPLICATION_YAML.toString());
    }

    protected abstract TupleSpaceApi<T, TT, K, V, M> getTupleSpaceApi(RoutingContext routingContext);

    public void post(RoutingContext routingContext) {
        final var api = getTupleSpaceApi(routingContext);
        final Future<Collection<? extends T>> result = Future.future();

        try {
            final String tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final Optional<Boolean> bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final List<T> tuples = parseTuples(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());

            final Tuple3<String, Boolean, List<T>> cleanInputs = validateInputsForPost(tupleSpaceName, bulk, tuples);

            result.setHandler(responseHandler(routingContext, response -> validateOutputsForPost(cleanInputs, response)));

            api.createNewTuples(cleanInputs.v1(), cleanInputs.v2(), cleanInputs.v3(), result.completer());
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    protected abstract List<T> parseTuples(String mimeType, String payload) throws IOException;

    private Tuple3<String, Boolean, List<T>> validateInputsForPost(String tupleSpaceName, Optional<Boolean> bulk, List<T> tuples) {
        final var bulkValue = bulk.orElse(false);

        if (!bulkValue && tuples.size() != 1) {
            throw new BadContentError();
        } else if (tuples.size() == 0) {
            throw new BadContentError();
        }

        return Tuple.tuple(
                Objects.requireNonNull(tupleSpaceName),
                bulkValue,
                tuples
            );
    }

    private Collection<? extends T> validateOutputsForPost(Tuple3<String, Boolean, List<T>> inputs, Collection<? extends T> output) {
        return validateOutputsForPost(inputs.v1(), inputs.v2(), inputs.v3(), output);
    }

    private Collection<? extends T> validateOutputsForPost(String tupleSpaceName, boolean bulk, List<T> input, Collection<? extends T> output) {
        if (input.size() != output.size()) {
            throw new InternalServerError();
        }

        return output;
    }

    public void delete(RoutingContext routingContext) {
        final var api = getTupleSpaceApi(routingContext);
        final Future<Collection<? extends M>> result = Future.future();

        try {
            final String tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final Optional<Boolean> bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final Optional<Boolean> predicative = Optional.ofNullable(routingContext.queryParams().get("predicative")).map(Boolean::parseBoolean);
            final TT template = parseTemplate(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());

            final Tuple4<String, Boolean, Boolean, TT> cleanInputs = validateInputsForDelete(tupleSpaceName, bulk, predicative, template);

            result.setHandler(responseHandler(routingContext, response -> validateOutputsForDelete(cleanInputs, response)));

            api.consumeTuples(cleanInputs.v1(), cleanInputs.v2(), cleanInputs.v3(), cleanInputs.v4(), result.completer());
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    protected abstract TT parseTemplate(String mimeType, String payload) throws IOException;

    private Tuple4<String, Boolean, Boolean, TT> validateInputsForDelete(String tupleSpaceName, Optional<Boolean> bulk, Optional<Boolean> predicative, TT template) {
        return Tuple.tuple(
                Objects.requireNonNull(tupleSpaceName),
                bulk.orElse(false),
                predicative.orElse(false),
                template
            );
    }

    private <ML extends Collection<? extends M>> ML validateOutputsForDelete(Tuple4<String, Boolean, Boolean, TT> inputs, ML output) {
        return validateOutputsForDelete(inputs.v1(), inputs.v2(), inputs.v3(), inputs.v4(), output);
    }

    private <ML extends Collection<? extends M>> ML validateOutputsForDelete(String tupleSpaceName, boolean bulk, boolean predicative, TT template, ML output) {

        if (!bulk && output.size() > 1) {
            throw new InternalServerError();
        }

        return output;
    }

    public void get(RoutingContext routingContext) {
        final var api = getTupleSpaceApi(routingContext);
        final Future<Collection<? extends M>> result = Future.future();

        try {
            final var tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final var bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final var predicative = Optional.ofNullable(routingContext.queryParams().get("predicative")).map(Boolean::parseBoolean);
            final var negated = Optional.ofNullable(routingContext.queryParams().get("negated")).map(Boolean::parseBoolean);
            final var template = parseTemplate(routingContext.parsedHeaders().contentType().value(), routingContext.getBodyAsString());

            final var cleanInputs = validateInputsForGet(tupleSpaceName, bulk, predicative, negated, template);

            result.setHandler(responseHandler(routingContext, response -> validateOutputsForGet(cleanInputs, response)));

            api.observeTuples(cleanInputs.v1(), cleanInputs.v2(), cleanInputs.v3(), cleanInputs.v4(), cleanInputs.v5(), result);
        } catch (HttpError e) {
            result.fail(e);
        } catch (IOException | IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private Tuple5<String, Boolean, Boolean, Boolean, TT> validateInputsForGet(String tupleSpaceName, Optional<Boolean> bulk, Optional<Boolean> predicative, Optional<Boolean> negated, TT template) {
        return Tuple.tuple(
                Objects.requireNonNull(tupleSpaceName),
                bulk.orElse(false),
                predicative.orElse(false),
                negated.orElse(false),
                template
        );
    }

    private <TL extends Collection<? extends M>> TL validateOutputsForGet(Tuple5<String, Boolean, Boolean, Boolean, TT> inputs, TL output) {
        return validateOutputsForDelete(inputs.v1(), inputs.v2(), inputs.v3(), inputs.v4(), inputs.v5(), output);
    }

    private <TL extends Collection<? extends M>> TL validateOutputsForDelete(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, TT template, TL output) {

        if (!bulk && output.size() > 1) {
            throw new InternalServerError();
        }

        return output;
    }

}