package it.unibo.coordination.linda.logic.remote

import alice.tuprolog.Term
import io.vertx.core.Vertx
import io.vertx.core.http.*
import io.vertx.core.http.HttpMethod.*
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.coordination.linda.presentation.MIMETypes
import it.unibo.coordination.linda.presentation.Presentation
import org.apache.commons.collections4.MultiSet
import org.apache.commons.collections4.multiset.HashMultiSet
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture

internal class RemoteLogicSpaceImpl(private val serviceAddress: URL, private val name: String) : RemoteLogicSpace {

    companion object {

        @JvmStatic
        protected val vertx: Vertx by lazy {
            Vertx.vertx()
        }

        @JvmStatic
        protected val webClient: HttpClient by lazy {
            vertx.createHttpClient(HttpClientOptions().setKeepAlive(false))
        }

        @JvmStatic
        protected val tusowApiVersion: String = "1"

        @JvmStatic
        protected val mimeType = MIMETypes.APPLICATION_YAML

        @JvmStatic
        private inline infix fun String.assignTo(any: Any) = Pair(this, any)

        @JvmStatic
        private inline fun Collection<LogicTuple>.convertTo(type: MIMETypes): String =
                Presentation.getSerializer(LogicTuple::class.java, type).toString(this)

        @JvmStatic
        private inline fun LogicTemplate.convertTo(type: MIMETypes): String =
                Presentation.getSerializer(LogicTemplate::class.java, type).toString(this)

        @JvmStatic
        private fun <T> String.parseAs(clazz: Class<T>, type: MIMETypes): T {
            return Presentation.getDeserializer(clazz, type).fromString(this)
        }

        @JvmStatic
        private fun <T> String.parseAsListOf(clazz: Class<T>, type: MIMETypes): List<T> {
            return Presentation.getDeserializer(clazz, type).listFromString(this)
        }

        @JvmStatic
        private inline fun <T> HttpClientRequest.addExceptionHandler(future: CompletableFuture<T>) {
            this.exceptionHandler {
                future.completeExceptionally(it)
            }
        }

        @JvmStatic
        private inline fun <T> HttpClientResponse.addExceptionHandler(future: CompletableFuture<T>) {
            this.exceptionHandler {
                future.completeExceptionally(it)
            }
        }
    }

    val tupleSpacePath: String by lazy { "/tusow/v$tusowApiVersion/tuple-spaces/logic/$name" }

    override fun getName(): String {
        return name
    }

    override fun tryTake(template: LogicTemplate): CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> {
        val promise = CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>>()

        remoteOperation(
                method = DELETE,
                query = listOf("bulk" assignTo false, "predicative" assignTo true),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$DELETE", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(LogicMatch::class.java, mimeType)
                    if (matches.isEmpty()) {
                        promise.completeExceptionally(IllegalStateException("Wrong amount of results from $DELETE ${req.absoluteURI()}, got 0 expected 1"))
                    } else {
                        promise.complete(matches[0])
                    }
                }
            }
        }

        return promise
    }

    override fun take(template: LogicTemplate): CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> {
        val promise = CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>>()

        remoteOperation(
                method = DELETE,
                query = listOf("bulk" assignTo false, "predicative" assignTo false),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$DELETE", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(LogicMatch::class.java, mimeType)
                    if (matches.isEmpty()) {
                        promise.completeExceptionally(IllegalStateException("Wrong amount of results from $DELETE ${req.absoluteURI()}, got 0 expected 1"))
                    } else {
                        promise.complete(matches[0])
                    }
                }
            }
        }

        return promise
    }

    override fun takeAll(template: LogicTemplate): CompletableFuture<Collection<out Match<LogicTuple, LogicTemplate, String, Term>>> {
        val promise = CompletableFuture<Collection<out Match<LogicTuple, LogicTemplate, String, Term>>>()

        remoteOperation(
                method = DELETE,
                query = listOf("bulk" assignTo true, "predicative" assignTo false),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$DELETE", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(LogicMatch::class.java, mimeType)
                    promise.complete(matches)
                }
            }
        }

        return promise
    }

    override fun write(tuple: LogicTuple): CompletableFuture<LogicTuple> {
        val promise = CompletableFuture<LogicTuple>()

        remoteOperation(
                method = POST,
                query = listOf("bulk" assignTo false),
                body = { listOf(tuple).convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$POST", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val tuples = it.toString("UTF-8").parseAsListOf(LogicTuple::class.java, mimeType)
                    if (tuples.isEmpty()) {
                        promise.completeExceptionally(IllegalStateException("Wrong amount of results from $POST ${req.absoluteURI()}, got 0 expected 1"))
                    } else {
                        promise.complete(tuples[0])
                    }
                }
            }
        }

        return promise
    }

    override fun writeAll(tuples: Collection<out LogicTuple>): CompletableFuture<MultiSet<LogicTuple>> {
        val promise = CompletableFuture<MultiSet<LogicTuple>>()

        remoteOperation(
                method = POST,
                query = listOf("bulk" assignTo true),
                body = { tuples.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$POST", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val resultTuples = it.toString("UTF-8").parseAsListOf(LogicTuple::class.java, mimeType)
                    promise.complete(HashMultiSet(resultTuples))
                }
            }
        }

        return promise
    }

    override fun getSize(): CompletableFuture<Int> {
        val promise = CompletableFuture<Int>()

        remoteOperation(
                method = HEAD,
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$HEAD", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    try {
                      promise.complete(it.toString("UTF-8").toInt())
                    } catch (e: NumberFormatException) {
                        promise.completeExceptionally(e)
                    }
                }
            }
        }

        return promise
    }

    override fun readAll(template: LogicTemplate): CompletableFuture<Collection<out Match<LogicTuple, LogicTemplate, String, Term>>> {
        val promise = CompletableFuture<Collection<out Match<LogicTuple, LogicTemplate, String, Term>>>()

        remoteOperation(
                method = GET,
                query = listOf("bulk" assignTo true, "predicative" assignTo false, "negated" assignTo false, "all" assignTo false),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$GET", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(LogicMatch::class.java, mimeType)
                    promise.complete(matches)
                }
            }
        }

        return promise
    }

    override fun tryRead(template: LogicTemplate): CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> {
        val promise = CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>>()

        remoteOperation(
                method = GET,
                query = listOf("bulk" assignTo false, "predicative" assignTo true, "negated" assignTo false, "all" assignTo false),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$GET", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(LogicMatch::class.java, mimeType)
                    if (matches.isEmpty()) {
                        promise.completeExceptionally(IllegalStateException("Wrong amount of results from $GET ${req.absoluteURI()}, got 0 expected 1"))
                    } else {
                        promise.complete(matches[0])
                    }
                }
            }
        }

        return promise
    }

    override fun read(template: LogicTemplate): CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> {
        val promise = CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>>()

        remoteOperation(
                method = GET,
                query = listOf("bulk" assignTo false, "predicative" assignTo false, "negated" assignTo false, "all" assignTo false),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$GET", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(LogicMatch::class.java, mimeType)
                    if (matches.isEmpty()) {
                        promise.completeExceptionally(IllegalStateException("Wrong amount of results from $GET ${req.absoluteURI()}, got 0 expected 1"))
                    } else {
                        promise.complete(matches[0])
                    }
                }
            }
        }

        return promise
    }

    override fun tryAbsent(template: LogicTemplate): CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> {
        val promise = CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>>()

        remoteOperation(
                method = GET,
                query = listOf("bulk" assignTo false, "predicative" assignTo true, "negated" assignTo true, "all" assignTo false),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$GET", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(LogicMatch::class.java, mimeType)
                    if (matches.isEmpty()) {
                        promise.completeExceptionally(IllegalStateException("Wrong amount of results from $GET ${req.absoluteURI()}, got 0 expected 1"))
                    } else {
                        promise.complete(matches[0])
                    }
                }
            }
        }

        return promise
    }

    override fun absent(template: LogicTemplate): CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>> {
        val promise = CompletableFuture<Match<LogicTuple, LogicTemplate, String, Term>>()

        remoteOperation(
                method = GET,
                query = listOf("bulk" assignTo false, "predicative" assignTo false, "negated" assignTo true, "all" assignTo false),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$GET", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(LogicMatch::class.java, mimeType)
                    if (matches.isEmpty()) {
                        promise.completeExceptionally(IllegalStateException("Wrong amount of results from $GET ${req.absoluteURI()}, got 0 expected 1"))
                    } else {
                        promise.complete(matches[0])
                    }
                }
            }
        }

        return promise
    }

    override fun get(): CompletableFuture<MultiSet<LogicTuple>> {
        val promise = CompletableFuture<MultiSet<LogicTuple>>()

        remoteOperation(
                method = GET,
                query = listOf("bulk" assignTo false, "predicative" assignTo false, "negated" assignTo false, "all" assignTo true),
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$GET", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(LogicTuple::class.java, mimeType)
                    promise.complete(HashMultiSet(matches))
                }
            }
        }

        return promise
    }

    protected fun <T> remoteOperation(method: HttpMethod, query: Collection<Pair<String, Any>> = emptyList(), body: ()->String? = {null}, future: CompletableFuture<T>,  callback: (HttpClientRequest, HttpClientResponse)->Unit) {
        val path = tupleSpacePath + if (query.isEmpty()) "" else query.joinToString("&", "?"){
            "${it.first}=${URLEncoder.encode(it.second.toString(), "UTF-8")}"
        }

        val request = webClient.request(method, serviceAddress.port, serviceAddress.host, path)
                .putHeader(HttpHeaders.CONTENT_TYPE.toString(), mimeType.toString())
                .putHeader(HttpHeaders.ACCEPT.toString(), mimeType.toString())

        request.handler {
            callback(request, it)
        }
        request.addExceptionHandler(future)

        val bodyString = body()
        if (bodyString !== null) {
            request.end(bodyString)
        } else {
            request.end()
        }
    }
}