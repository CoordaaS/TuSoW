package it.unibo.coordination.linda.strings.remote

import io.vertx.core.Vertx
import io.vertx.core.http.*
import io.vertx.core.http.HttpMethod.*
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.presentation.Deserializer
import it.unibo.coordination.linda.presentation.MIMETypes
import it.unibo.coordination.linda.presentation.Serializer
import it.unibo.coordination.linda.string.RegexTemplate
import it.unibo.coordination.linda.string.RegularMatch
import it.unibo.coordination.linda.string.StringTuple
import org.apache.commons.collections4.multiset.HashMultiSet
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture

internal class RemoteStringSpaceImpl(private val serviceAddress: URL, private val _name: String) : RemoteStringSpace {

    companion object {

        @JvmStatic
        protected val vertx: Vertx by lazy {
            Vertx.vertx()
        }

        @JvmStatic
        protected val webClient: HttpClient by lazy {
            vertx.createHttpClient(
                    HttpClientOptions()
                            .setKeepAlive(true)
                            .setHttp2MaxPoolSize(1 shl 15)
                            .setMaxPoolSize(1 shl 15)
            )
        }

        @JvmStatic
        protected val tusowApiVersion: String = "1"

        @JvmStatic
        protected val mimeType = MIMETypes.APPLICATION_YAML

        @JvmStatic
        private infix fun String.assignTo(any: Any) = Pair(this, any)

        @JvmStatic
        private fun Collection<StringTuple>.convertTo(type: MIMETypes): String =
                Serializer.of(StringTuple::class.java, type).toString(this)

        @JvmStatic
        private fun RegexTemplate.convertTo(type: MIMETypes): String =
                Serializer.of(RegexTemplate::class.java, type).toString(this)

        @JvmStatic
        private fun <T> String.parseAs(clazz: Class<T>, type: MIMETypes): T {
            return Deserializer.of(clazz, type).fromString(this)
        }

        @JvmStatic
        private fun <T> String.parseAsListOf(clazz: Class<T>, type: MIMETypes): List<T> {
            return Deserializer.of(clazz, type).listFromString(this)
        }

        @JvmStatic
        private fun <T> HttpClientRequest.addExceptionHandler(future: CompletableFuture<T>) {
            this.exceptionHandler {
                future.completeExceptionally(it)
            }
        }

        @JvmStatic
        private fun <T> HttpClientResponse.addExceptionHandler(future: CompletableFuture<T>) {
            this.exceptionHandler {
                future.completeExceptionally(it)
            }
        }
    }

    val tupleSpacePath: String by lazy { "/tusow/v$tusowApiVersion/tuple-spaces/textual/$name" }

    override val name: String
        get() = _name

    override fun tryTake(template: RegexTemplate): CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>> {
        val promise = CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(RegularMatch::class.java, Companion.mimeType)
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

    override fun take(template: RegexTemplate): CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>> {
        val promise = CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(RegularMatch::class.java, Companion.mimeType)
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

    override fun takeAll(template: RegexTemplate): CompletableFuture<Collection<Match<StringTuple, RegexTemplate, Any, String>>> {
        val promise = CompletableFuture<Collection<Match<StringTuple, RegexTemplate, Any, String>>>()

        remoteOperation(
                method = DELETE,
                query = listOf("bulk" assignTo true, "predicative" assignTo false),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() == 204) {
                promise.complete(emptyList())
            } else if (res.statusCode() !in setOf(200, 204)) {
                promise.completeExceptionally(RemoteException("$DELETE", req.absoluteURI(), res.statusCode()))
            }  else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(RegularMatch::class.java, Companion.mimeType)
                    promise.complete(matches)
                }
            }
        }

        return promise
    }

    override fun write(tuple: StringTuple): CompletableFuture<StringTuple> {
        val promise = CompletableFuture<StringTuple>()

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
                    val tuples = it.toString("UTF-8").parseAsListOf(StringTuple::class.java, Companion.mimeType)
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

    override fun writeAll(tuples: Collection<StringTuple>): CompletableFuture<Collection<StringTuple>> {
        val promise = CompletableFuture<Collection<StringTuple>>()

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
                    val resultTuples = it.toString("UTF-8").parseAsListOf(StringTuple::class.java, Companion.mimeType)
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
            when {
                res.statusCode() != 200 ->
                    promise.completeExceptionally(RemoteException("$HEAD", req.absoluteURI(), res.statusCode()))
                "X-TUPLE-SPACE-SIZE" in res.headers() ->
                    try {
                        promise.complete(res.getHeader("X-TUPLE-SPACE-SIZE").toInt())
                    } catch (e: NumberFormatException) {
                        promise.completeExceptionally(e)
                    }
                else ->
                    promise.completeExceptionally(IllegalStateException("Missing response header X-TUPLE-SPACE-SIZE"))
            }
        }

        return promise
    }

    override fun readAll(template: RegexTemplate): CompletableFuture<Collection<Match<StringTuple, RegexTemplate, Any, String>>> {
        val promise = CompletableFuture<Collection<Match<StringTuple, RegexTemplate, Any, String>>>()

        remoteOperation(
                method = GET,
                query = listOf("bulk" assignTo true, "predicative" assignTo false, "negated" assignTo false, "all" assignTo false),
                body = { template.convertTo(mimeType) },
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() == 204) {
                promise.complete(emptyList())
            } else if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$DELETE", req.absoluteURI(), res.statusCode()))
            }  else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(RegularMatch::class.java, Companion.mimeType)
                    promise.complete(matches)
                }
            }
        }

        return promise
    }

    override fun tryRead(template: RegexTemplate): CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>> {
        val promise = CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(RegularMatch::class.java, Companion.mimeType)
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

    override fun read(template: RegexTemplate): CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>> {
        val promise = CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(RegularMatch::class.java, Companion.mimeType)
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

    override fun tryAbsent(template: RegexTemplate): CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>> {
        val promise = CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(RegularMatch::class.java, Companion.mimeType)
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

    override fun absent(template: RegexTemplate): CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>> {
        val promise = CompletableFuture<Match<StringTuple, RegexTemplate, Any, String>>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(RegularMatch::class.java, Companion.mimeType)
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

    override fun get(): CompletableFuture<Collection<StringTuple>> {
        val promise = CompletableFuture<Collection<StringTuple>>()

        remoteOperation(
                method = GET,
                query = listOf("bulk" assignTo false, "predicative" assignTo false, "negated" assignTo false, "all" assignTo true),
                future = promise
        ) { req, res ->
            res.addExceptionHandler(promise)
            if (res.statusCode() == 204) {
                promise.complete(HashMultiSet())
            } else if (res.statusCode() != 200) {
                promise.completeExceptionally(RemoteException("$GET", req.absoluteURI(), res.statusCode()))
            } else {
                res.bodyHandler {
                    val matches = it.toString("UTF-8").parseAsListOf(StringTuple::class.java, Companion.mimeType)
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

        @Suppress("DEPRECATION")
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