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
import java.util.*

@Suppress("UNUSED_PARAMETER")
abstract class AbstractTupleSpacePath<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>(tupleSpaceType: String) : Path("/$tupleSpaceType/:tupleSpaceName") {

    override fun setupRoutes() {
        addRoute(HttpMethod.DELETE, Handler { delete(it) })
                .consumes(MIMETypes.APPLICATION_JSON.toString())
                .consumes(MIMETypes.APPLICATION_XML.toString())
                .consumes(MIMETypes.APPLICATION_YAML.toString())
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())

        addRoute(HttpMethod.GET, Handler { get(it) })
                .consumes(MIMETypes.APPLICATION_JSON.toString())
                .consumes(MIMETypes.APPLICATION_XML.toString())
                .consumes(MIMETypes.APPLICATION_YAML.toString())
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())

        addRoute(HttpMethod.POST, Handler { post(it) })
                .consumes(MIMETypes.APPLICATION_JSON.toString())
                .consumes(MIMETypes.APPLICATION_XML.toString())
                .consumes(MIMETypes.APPLICATION_YAML.toString())
                .produces(MIMETypes.APPLICATION_JSON.toString())
                .produces(MIMETypes.APPLICATION_XML.toString())
                .produces(MIMETypes.APPLICATION_YAML.toString())

        addRoute(HttpMethod.HEAD, Handler { head(it) })
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
            api.createNewTuples(cleanInputs.tupleSpaceName, cleanInputs.bulk, cleanInputs.tuples, result)
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

    private data class InputsForPost<T>(val tupleSpaceName: String, val bulk: Boolean, val tuples: List<T>)

    private fun validateInputsForPost(tupleSpaceName: String, bulk: Optional<Boolean>, tuples: List<T>): InputsForPost<T> {
        val bulkValue = bulk.orElse(false)
        return if (!bulkValue && tuples.size != 1) {
            throw BadContentError()
        } else if (tuples.isEmpty()) {
            throw BadContentError()
        } else {
            InputsForPost(
                    Objects.requireNonNull(tupleSpaceName),
                    bulkValue,
                    tuples
            )
        }
    }

    private fun validateOutputsForPost(inputs: InputsForPost<T>, output: Collection<T>): Collection<T> {
        if (inputs.tuples.size != output.size) {
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
            api.consumeTuples(cleanInputs.tupleSpaceName, cleanInputs.bulk, cleanInputs.predicative, cleanInputs.template, result)
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

    private data class InputsForDelete<TT>(
            val tupleSpaceName: String,
            val bulk: Boolean,
            val predicative: Boolean,
            val template: TT
    )

    private fun validateInputsForDelete(tupleSpaceName: String, bulk: Optional<Boolean>, predicative: Optional<Boolean>, template: TT): InputsForDelete<TT> {
        return InputsForDelete(
                tupleSpaceName,
                bulk.orElse(false),
                predicative.orElse(false),
                template
        )
    }

    private fun <ML : Collection<M>> validateOutputsForDelete(inputs: InputsForDelete<TT>, output: ML): ML {
        if (!inputs.bulk && output.size > 1) {
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
            if (cleanInputs.all) {
                val res: Promise<Collection<T>> = Promise.promise()
                res.future().onSuccess(
                        successfulResponseHandlerWithManyContents(routingContext, this::getTuplesMarshaller) {
                            validateOutputsForGetAll(cleanInputs, it)
                        }
                )
                result = res
                api.getAllTuples(cleanInputs.tupleSpaceName, res)
            } else {
                val res: Promise<Collection<M>> = Promise.promise()
                res.future().onSuccess(
                        successfulResponseHandlerWithManyContents(routingContext, this::getMatchMarshaller) {
                            validateOutputsForGet(cleanInputs, it)
                        }
                )
                result = res
                api.observeTuples(cleanInputs.tupleSpaceName, cleanInputs.bulk, cleanInputs.predicative, cleanInputs.negated, cleanInputs.template!!, res)
            }
        } catch (e: HttpError) {
            result.fail(e)
        } catch (e: IllegalArgumentException) {
            result.fail(BadContentError(e))
        }
    }

    private data class InputsForGet<TT>(
            val tupleSpaceName: String,
            val bulk: Boolean,
            val predicative: Boolean,
            val negated: Boolean,
            val all: Boolean,
            val template: TT?
    )

    private fun validateInputsForGet(tupleSpaceName: String, bulk: Optional<Boolean>, predicative: Optional<Boolean>, negated: Optional<Boolean>, all: Optional<Boolean>, template: TT?): InputsForGet<TT> {
        if (template == null && Optional.of(true) != all) {
            throw BadContentError("The lack of body for GET is only supported in case query parameter `all` is true")
        }
        return InputsForGet(
                Objects.requireNonNull(tupleSpaceName),
                bulk.orElse(false),
                predicative.orElse(false),
                negated.orElse(false),
                all.orElse(false),
                template
        )
    }

    private fun <TL : Collection<M>> validateOutputsForGet(inputs: InputsForGet<TT>, output: TL): TL {
        if (!inputs.bulk && !inputs.all && output.size > 1) {
            throw InternalServerError()
        }
        return output
    }

    private fun <TL : Collection<T>> validateOutputsForGetAll(inputs: InputsForGet<TT>, output: TL): TL {
        if (!inputs.bulk && !inputs.all && output.size > 1) {
            throw InternalServerError()
        }
        return output
    }
}