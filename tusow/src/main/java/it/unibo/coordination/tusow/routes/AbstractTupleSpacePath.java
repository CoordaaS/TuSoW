package it.unibo.coordination.tusow.routes;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.presentation.Deserializer;
import it.unibo.coordination.linda.presentation.MIMETypes;
import it.unibo.coordination.linda.presentation.Serializer;
import it.unibo.coordination.tusow.api.TupleSpaceApi;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.HttpError;
import it.unibo.coordination.tusow.exceptions.InternalServerError;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.jooq.lambda.tuple.Tuple6;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static it.unibo.coordination.linda.presentation.MIMETypes.*;

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

        addRoute(HttpMethod.HEAD, this::head)
                .consumes(ANY.toString())
                .produces(APPLICATION_JSON.toString())
                .produces(APPLICATION_XML.toString())
                .produces(APPLICATION_YAML.toString());
    }

    protected abstract TupleSpaceApi<T, TT, K, V, M> getTupleSpaceApi(RoutingContext routingContext);

    protected abstract <N extends Number> T numberToTuple(N x);

    public void post(RoutingContext routingContext) {
        final var api = getTupleSpaceApi(routingContext);
        final Future<Collection<? extends T>> result = Future.future();
        result.setHandler(responseHandlerWithNoContent(routingContext));

        try {
            final String tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final Optional<Boolean> bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final MIMETypes mimeType = MIMETypes.parse(routingContext.parsedHeaders().contentType().value());
            final List<T> tuples = getTuplesUnmarshaller(mimeType).listFromString(routingContext.getBodyAsString());

            final Tuple3<String, Boolean, List<T>> cleanInputs = validateInputsForPost(tupleSpaceName, bulk, tuples);

            result.setHandler(responseHandlerWithManyContents(routingContext, this::getTuplesMarshaller, response -> validateOutputsForPost(cleanInputs, response)));

            api.createNewTuples(cleanInputs.v1(), cleanInputs.v2(), cleanInputs.v3(), result.completer());
        } catch (HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    public void head(RoutingContext routingContext) {
        final var api = getTupleSpaceApi(routingContext);
        final Future<Integer> result = Future.future();
        result.setHandler(responseHandlerWithNumericContent(routingContext, "X-TUPLE-SPACE-SIZE"));

        try {
            final String tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            
            api.countTuples(tupleSpaceName, result);
        } catch (HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private Collection<? extends T> validateOutputsForPost(Tuple3<String, Boolean, List<T>> inputs, Collection<? extends T> output) {
        return validateOutputsForPost(inputs.v1(), inputs.v2(), inputs.v3(), output);
    }

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

    private Collection<? extends T> validateOutputsForPost(String tupleSpaceName, boolean bulk, List<T> input, Collection<? extends T> output) {
        if (input.size() != output.size()) {
            throw new InternalServerError();
        }

        return output;
    }

    public void delete(RoutingContext routingContext) {
        final var api = getTupleSpaceApi(routingContext);
        final Future<Collection<? extends M>> result = Future.future();
        result.setHandler(responseHandlerWithNoContent(routingContext));

        try {
            final String tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final Optional<Boolean> bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final Optional<Boolean> predicative = Optional.ofNullable(routingContext.queryParams().get("predicative")).map(Boolean::parseBoolean);

            final MIMETypes mimeType = MIMETypes.parse(routingContext.parsedHeaders().contentType().value());
            final TT template = getTemplatesUnmarshaller(mimeType).fromString(routingContext.getBodyAsString());

            final Tuple4<String, Boolean, Boolean, TT> cleanInputs = validateInputsForDelete(tupleSpaceName, bulk, predicative, template);

            result.setHandler(responseHandlerWithManyContents(routingContext, this::getMatchMarshaller, response -> validateOutputsForDelete(cleanInputs, response)));

            api.consumeTuples(cleanInputs.v1(), cleanInputs.v2(), cleanInputs.v3(), cleanInputs.v4(), result.completer());
        } catch (HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    protected abstract Serializer<T> getTuplesMarshaller(MIMETypes mimeType);
    protected abstract Serializer<TT> getTemplatesMarshaller(MIMETypes mimeType);
    protected abstract Serializer<M> getMatchMarshaller(MIMETypes mimeType);

    protected abstract Deserializer<T> getTuplesUnmarshaller(MIMETypes mimeType);
    protected abstract Deserializer<TT> getTemplatesUnmarshaller(MIMETypes mimeType);
    protected abstract Deserializer<M> getMatchUnmarshaller(MIMETypes mimeType);

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
        Future<?> result = Future.future();
        result.setHandler(responseHandlerWithNoContent(routingContext));

        try {
            final var tupleSpaceName = routingContext.pathParam("tupleSpaceName");
            final var bulk = Optional.ofNullable(routingContext.queryParams().get("bulk")).map(Boolean::parseBoolean);
            final var predicative = Optional.ofNullable(routingContext.queryParams().get("predicative")).map(Boolean::parseBoolean);
            final var negated = Optional.ofNullable(routingContext.queryParams().get("negated")).map(Boolean::parseBoolean);
            final var all = Optional.ofNullable(routingContext.queryParams().get("all")).map(Boolean::parseBoolean);

            final MIMETypes mimeType = MIMETypes.parse(routingContext.parsedHeaders().contentType().value());

            TT template = null;
            if (routingContext.getBody().length() > 0) {
                template = getTemplatesUnmarshaller(mimeType).fromString(routingContext.getBodyAsString());
            }

            final var cleanInputs = validateInputsForGet(tupleSpaceName, bulk, predicative, negated, all, template);

            if (cleanInputs.v5()) {
                final Future<Collection<? extends T>> res = Future.future();
                res.setHandler(responseHandlerWithManyContents(routingContext, this::getTuplesMarshaller, response -> validateOutputsForGetAll(cleanInputs, response)));

                api.getAllTuples(cleanInputs.v1(), res);
                result = res;
            } else {
                final Future<Collection<? extends M>> res = Future.future();
                res.setHandler(responseHandlerWithManyContents(routingContext, this::getMatchMarshaller, response -> validateOutputsForGet(cleanInputs, response)));

                api.observeTuples(cleanInputs.v1(), cleanInputs.v2(), cleanInputs.v3(), cleanInputs.v4(), cleanInputs.v6(), res);
                result = res;
            }

        } catch (HttpError e) {
            result.fail(e);
        } catch (IllegalArgumentException e) {
            result.fail(new BadContentError(e));
        }
    }

    private Tuple6<String, Boolean, Boolean, Boolean, Boolean, TT> validateInputsForGet(String tupleSpaceName, Optional<Boolean> bulk, Optional<Boolean> predicative, Optional<Boolean> negated, Optional<Boolean> all, TT template) {

        if (template == null && !Optional.of(true).equals(all)) {
            throw new BadContentError("The lack of body for GET is only supported in case query parameter `all` is true");
        }

        return Tuple.tuple(
                Objects.requireNonNull(tupleSpaceName),
                bulk.orElse(false),
                predicative.orElse(false),
                negated.orElse(false),
                all.orElse(false),
                template
        );
    }

    private <TL extends Collection<? extends M>> TL validateOutputsForGet(Tuple6<String, Boolean, Boolean, Boolean, Boolean, TT> inputs, TL output) {
        return validateOutputsForGet(inputs.v1(), inputs.v2(), inputs.v3(), inputs.v4(), inputs.v5(), inputs.v6(), output);
    }

    private <TL extends Collection<? extends M>> TL validateOutputsForGet(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, boolean all, TT template, TL output) {

        if (!bulk && !all && output.size() > 1) {
            throw new InternalServerError();
        }

        return output;
    }

    private <TL extends Collection<? extends T>> TL validateOutputsForGetAll(Tuple6<String, Boolean, Boolean, Boolean, Boolean, TT> inputs, TL output) {
        return validateOutputsForGetAll(inputs.v1(), inputs.v2(), inputs.v3(), inputs.v4(), inputs.v5(), inputs.v6(), output);
    }

    private <TL extends Collection<? extends T>> TL validateOutputsForGetAll(String tupleSpaceName, boolean bulk, boolean predicative, boolean negated, boolean all, TT template, TL output) {

        if (!bulk && !all && output.size() > 1) {
            throw new InternalServerError();
        }

        return output;
    }

}