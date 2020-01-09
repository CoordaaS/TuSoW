package it.unibo.coordination.tusow.routes

import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.tusow.api.TupleSpaceApi
import it.unibo.coordination.tusow.exceptions.BadContentError
import it.unibo.coordination.tusow.exceptions.HttpError
import it.unibo.coordination.tusow.exceptions.InternalServerError
import it.unibo.presentation.Deserializer
import it.unibo.presentation.MIMETypes
import it.unibo.presentation.MIMETypes.Companion.parse
import it.unibo.presentation.Serializer
import org.jooq.lambda.tuple.Tuple3
import org.jooq.lambda.tuple.Tuple4
import org.jooq.lambda.tuple.Tuple6
import java.util.*

abstract class AbstractTupleSpacePath<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>(tupleSpaceType: String) : Path("/$tupleSpaceType/:tupleSpaceName") {

    override fun setupRoutes() {
        addRoute(HttpMethod.DELETE, Handler { routingContext: RoutingContext -> delete(routingContext) })
                .consumes(MIMETypes.APPLICATION_JSON.toString())
                .consumes(MIMETypes.APPLICATION_XML.toString())
                .consumes(MIMETypes.APPLICATION_YAML.toString())
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())

        addRoute(HttpMethod.GET, Handler { routingContext: RoutingContext -> this[routingContext] })
                .consumes(MIMETypes.APPLICATION_JSON.toString())
                .consumes(MIMETypes.APPLICATION_XML.toString())
                .consumes(MIMETypes.APPLICATION_YAML.toString())
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())

        addRoute(HttpMethod.POST, Handler { routingContext: RoutingContext -> post(routingContext) })
                .consumes(MIMETypes.APPLICATION_JSON.toString())
                .consumes(MIMETypes.APPLICATION_XML.toString())
                .consumes(MIMETypes.APPLICATION_YAML.toString())
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())

        addRoute(HttpMethod.HEAD, Handler { routingContext: RoutingContext -> head(routingContext) })
                .consumes(MIMETypes.ANY.toString())
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())
    }

    protected abstract fun getTupleSpaceApi(routingContext: RoutingContext): TupleSpaceApi<T, TT, K, V, M>

    protected abstract fun <N : Number> numberToTuple(x: N): T

    fun post(routingContext: RoutingContext) {
        val api = getTupleSpaceApi(routingContext)
        val result: Promise<Collection<T>> = Promise.promise()
        result.future().onFailure(responseHandlerFallback(routingContext))
        try {
            val tupleSpaceName = routingContext.pathParam("tupleSpaceName")
            val bulk = Optional.ofNullable(routingContext.queryParams()["bulk"]).map { it!!.toBoolean() }
            val mimeType = parse(routingContext.parsedHeaders().contentType().value())
            val tuples = getTuplesUnmarshaller(mimeType).listFromString(routingContext.bodyAsString)
            val cleanInputs = validateInputsForPost(tupleSpaceName, bulk, tuples)
            result.future().onSuccess(
                    successfulResponseHandlerWithManyContents(routingContext, this::getTuplesMarshaller) {
                        validateOutputsForPost(cleanInputs, it)
                    }
            )
            api.createNewTuples(cleanInputs.v1(), cleanInputs.v2(), cleanInputs.v3(), result)
        } catch (e: HttpError) {
            result.fail(e)
        } catch (e: IllegalArgumentException) {
            result.fail(BadContentError(e))
        }
    }

    fun head(routingContext: RoutingContext) {
        val api = getTupleSpaceApi(routingContext)
        val result = Promise.promise<Int>()
        result.future().onComplete(responseHandlerWithNumericContent(routingContext, "X-TUPLE-SPACE-SIZE"))
        try {
            val tupleSpaceName = routingContext.pathParam("tupleSpaceName")
            api.countTuples(tupleSpaceName, result)
        } catch (e: HttpError) {
            result.fail(e)
        } catch (e: IllegalArgumentException) {
            result.fail(BadContentError(e))
        }
    }

    private fun validateOutputsForPost(inputs: Tuple3<String, Boolean, List<T>>, output: Collection<T>): Collection<T> {
        return validateOutputsForPost(inputs.v1(), inputs.v2(), inputs.v3(), output)
    }

    private fun validateInputsForPost(tupleSpaceName: String, bulk: Optional<Boolean>, tuples: List<T>): Tuple3<String, Boolean, List<T>> {
        val bulkValue = bulk.orElse(false)
        if (!bulkValue && tuples.size != 1) {
            throw BadContentError()
        } else if (tuples.size == 0) {
            throw BadContentError()
        }
        return org.jooq.lambda.tuple.Tuple.tuple(
                Objects.requireNonNull(tupleSpaceName),
                bulkValue,
                tuples
        )
    }

    private fun validateOutputsForPost(tupleSpaceName: String, bulk: Boolean, input: List<T>, output: Collection<T>): Collection<T> {
        if (input.size != output.size) {
            throw InternalServerError()
        }
        return output
    }

    fun delete(routingContext: RoutingContext) {
        val api = getTupleSpaceApi(routingContext)
        val result: Promise<Collection<M>> = Promise.promise()
        result.future().onFailure(responseHandlerFallback(routingContext))
        try {
            val tupleSpaceName = routingContext.pathParam("tupleSpaceName")
            val bulk = Optional.ofNullable(routingContext.queryParams()["bulk"]).map { s: String -> java.lang.Boolean.parseBoolean(s) }
            val predicative = Optional.ofNullable(routingContext.queryParams()["predicative"]).map { s: String -> java.lang.Boolean.parseBoolean(s) }
            val mimeType = parse(routingContext.parsedHeaders().contentType().value())
            val template = getTemplatesUnmarshaller(mimeType).fromString(routingContext.bodyAsString)
            val cleanInputs = validateInputsForDelete(tupleSpaceName, bulk, predicative, template)
            result.future().onSuccess(
                    successfulResponseHandlerWithManyContents(routingContext, this::getMatchMarshaller) {
                        validateOutputsForDelete(cleanInputs, it)
                    }
            )
            api.consumeTuples(cleanInputs.v1(), cleanInputs.v2(), cleanInputs.v3(), cleanInputs.v4(), result)
        } catch (e: HttpError) {
            result.fail(e)
        } catch (e: IllegalArgumentException) {
            result.fail(BadContentError(e))
        }
    }

    protected abstract fun getTuplesMarshaller(mimeType: MIMETypes): Serializer<T>

    protected abstract fun getTemplatesMarshaller(mimeType: MIMETypes): Serializer<TT>

    protected abstract fun getMatchMarshaller(mimeType: MIMETypes): Serializer<M>

    protected abstract fun getTuplesUnmarshaller(mimeType: MIMETypes): Deserializer<T>

    protected abstract fun getTemplatesUnmarshaller(mimeType: MIMETypes): Deserializer<TT>

    protected abstract fun getMatchUnmarshaller(mimeType: MIMETypes): Deserializer<M>

    private fun validateInputsForDelete(tupleSpaceName: String, bulk: Optional<Boolean>, predicative: Optional<Boolean>, template: TT): Tuple4<String, Boolean, Boolean, TT> {
        return org.jooq.lambda.tuple.Tuple.tuple(
                tupleSpaceName,
                bulk.orElse(false),
                predicative.orElse(false),
                template
        )
    }

    private fun <ML : Collection<M>> validateOutputsForDelete(inputs: Tuple4<String, Boolean, Boolean, TT>, output: ML): ML {
        return validateOutputsForDelete(inputs.v1(), inputs.v2(), inputs.v3(), inputs.v4(), output)
    }

    private fun <ML : Collection<M>> validateOutputsForDelete(tupleSpaceName: String, bulk: Boolean, predicative: Boolean, template: TT, output: ML): ML {
        if (!bulk && output.size > 1) {
            throw InternalServerError()
        }
        return output
    }

    operator fun get(routingContext: RoutingContext) {
        val api = getTupleSpaceApi(routingContext)
        var result: Promise<*> = Promise.promise<Any>()
        result.future().onFailure(responseHandlerFallback(routingContext))
        try {
            val tupleSpaceName = routingContext.pathParam("tupleSpaceName")
            val bulk = Optional.ofNullable(routingContext.queryParams()["bulk"]).map { it!!.toBoolean() }
            val predicative = Optional.ofNullable(routingContext.queryParams()["predicative"]).map { it!!.toBoolean() }
            val negated = Optional.ofNullable(routingContext.queryParams()["negated"]).map { it!!.toBoolean() }
            val all = Optional.ofNullable(routingContext.queryParams()["all"]).map { it!!.toBoolean() }
            val mimeType = parse(routingContext.parsedHeaders().contentType().value())
            var template: TT? = null
            if (routingContext.body.length() > 0) {
                template = getTemplatesUnmarshaller(mimeType).fromString(routingContext.bodyAsString)
            }
            val cleanInputs = validateInputsForGet(tupleSpaceName, bulk, predicative, negated, all, template)
            if (cleanInputs.v5()) {
                val res: Promise<Collection<T>> = Promise.promise()
                res.future().onSuccess(
                        successfulResponseHandlerWithManyContents(routingContext,this::getTuplesMarshaller) {
                            validateOutputsForGetAll(cleanInputs, it)
                        }
                )
                result = res
                api.getAllTuples(cleanInputs.v1(), res)
            } else {
                val res: Promise<Collection<M>> = Promise.promise()
                res.future().onSuccess(
                        successfulResponseHandlerWithManyContents(routingContext,this::getMatchMarshaller) {
                            validateOutputsForGet(cleanInputs, it)
                        }
                )
                result = res
                api.observeTuples(cleanInputs.v1(), cleanInputs.v2(), cleanInputs.v3(), cleanInputs.v4(), cleanInputs.v6(), res)
            }
        } catch (e: HttpError) {
            result.fail(e)
        } catch (e: IllegalArgumentException) {
            result.fail(BadContentError(e))
        }
    }

    private fun validateInputsForGet(tupleSpaceName: String, bulk: Optional<Boolean>, predicative: Optional<Boolean>, negated: Optional<Boolean>, all: Optional<Boolean>, template: TT?): Tuple6<String, Boolean, Boolean, Boolean, Boolean, TT> {
        if (template == null && Optional.of(true) != all) {
            throw BadContentError("The lack of body for GET is only supported in case query parameter `all` is true")
        }
        return org.jooq.lambda.tuple.Tuple.tuple(
                Objects.requireNonNull(tupleSpaceName),
                bulk.orElse(false),
                predicative.orElse(false),
                negated.orElse(false),
                all.orElse(false),
                template
        )
    }

    private fun <TL : Collection<M>> validateOutputsForGet(inputs: Tuple6<String, Boolean, Boolean, Boolean, Boolean, TT>, output: TL): TL {
        return validateOutputsForGet(inputs.v1(), inputs.v2(), inputs.v3(), inputs.v4(), inputs.v5(), inputs.v6(), output)
    }

    private fun <TL : Collection<M>> validateOutputsForGet(tupleSpaceName: String, bulk: Boolean, predicative: Boolean, negated: Boolean, all: Boolean, template: TT, output: TL): TL {
        if (!bulk && !all && output.size > 1) {
            throw InternalServerError()
        }
        return output
    }

    private fun <TL : Collection<T>> validateOutputsForGetAll(inputs: Tuple6<String, Boolean, Boolean, Boolean, Boolean, TT>, output: TL): TL {
        return validateOutputsForGetAll(inputs.v1(), inputs.v2(), inputs.v3(), inputs.v4(), inputs.v5(), inputs.v6(), output)
    }

    private fun <TL : Collection<T>> validateOutputsForGetAll(tupleSpaceName: String, bulk: Boolean, predicative: Boolean, negated: Boolean, all: Boolean, template: TT, output: TL): TL {
        if (!bulk && !all && output.size > 1) {
            throw InternalServerError()
        }
        return output
    }
}