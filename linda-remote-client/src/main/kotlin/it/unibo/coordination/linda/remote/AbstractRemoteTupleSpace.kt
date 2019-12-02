package it.unibo.coordination.linda.remote

import io.vertx.core.Vertx
import io.vertx.core.http.*
import io.vertx.core.http.HttpMethod.*
import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.presentation.Deserializer
import it.unibo.coordination.linda.presentation.MIMETypes
import it.unibo.coordination.linda.presentation.Serializer
import org.apache.commons.collections4.multiset.HashMultiSet
import java.net.URL
import java.net.URLEncoder

abstract class AbstractRemoteTupleSpace<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>>
    protected constructor(override val service: URL, name: String) : RemoteTupleSpace<T, TT, K, V, M> {

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
        protected infix fun String.assignTo(any: Any) = Pair(this, any)

        @JvmStatic
        protected fun<X> HttpClientRequest.addExceptionHandler(future: Promise<X>) {
            this.exceptionHandler {
                future.completeExceptionally(it)
            }
        }

        @JvmStatic
        protected fun <X> HttpClientResponse.addExceptionHandler(future: Promise<X>) {
            this.exceptionHandler {
                future.completeExceptionally(it)
            }
        }

        @JvmStatic
        protected fun <X> String.parseAs(clazz: Class<X>, type: MIMETypes): X {
            return Deserializer.of(clazz, type).fromString(this)
        }

        @JvmStatic
        protected fun <X> String.parseAsListOf(clazz: Class<X>, type: MIMETypes): List<X> {
            return Deserializer.of(clazz, type).listFromString(this)
        }
    }

    val tupleSpacePath: String by lazy { "/tusow/v$tusowApiVersion/tuple-spaces/$tupleSpaceType/$name" }

    abstract val tupleSpaceType: String

    override val url: URL by lazy {
        URL(service, tupleSpacePath)
    }

    override val name: String = name

    protected abstract val tupleClass: Class<T>
    protected abstract val templateClass: Class<TT>
    protected abstract val matchClass: Class<M>

    protected fun Collection<T>.convertTo(type: MIMETypes): String =
            Serializer.of(tupleClass, type).toString(this)

    protected fun TT.convertTo(type: MIMETypes): String =
            Serializer.of(templateClass, type).toString(this)
    

    override fun tryTake(template: TT): Promise<M> {
        val promise = Promise<M>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(matchClass, mimeType)
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

    override fun take(template: TT): Promise<M> {
        val promise = Promise<M>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(matchClass, mimeType)
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

    override fun takeAll(template: TT): Promise<Collection<M>> {
        val promise = Promise<Collection<M>>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(matchClass, mimeType)
                    promise.complete(matches)
                }
            }
        }


        return promise
    }

    override fun write(tuple: T): Promise<T> {
        val promise = Promise<T>()

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
                    val tuples = it.toString("UTF-8").parseAsListOf(tupleClass, mimeType)
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

    override fun writeAll(tuples: Collection<T>): Promise<Collection<T>> {
        val promise = Promise<Collection<T>>()

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
                    val resultTuples = it.toString("UTF-8").parseAsListOf(tupleClass, mimeType)
                    promise.complete(HashMultiSet(resultTuples))
                }
            }
        }

        return promise
    }

    override fun getSize(): Promise<Int> {
        val promise = Promise<Int>()

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

    override fun readAll(template: TT): Promise<Collection<M>> {
        val promise = Promise<Collection<M>>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(matchClass, mimeType)
                    promise.complete(matches)
                }
            }
        }

        return promise
    }

    override fun tryRead(template: TT): Promise<M> {
        val promise = Promise<M>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(matchClass, mimeType)
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

    override fun read(template: TT): Promise<M> {
        val promise = Promise<M>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(matchClass, mimeType)
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

    override fun tryAbsent(template: TT): Promise<M> {
        val promise = Promise<M>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(matchClass, mimeType)
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

    override fun absent(template: TT): Promise<M> {
        val promise = Promise<M>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(matchClass, mimeType)
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

    override fun get(): Promise<Collection<T>> {
        val promise = Promise<Collection<T>>()

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
                    val matches = it.toString("UTF-8").parseAsListOf(tupleClass, mimeType)
                    promise.complete(HashMultiSet(matches))
                }
            }
        }

        return promise
    }

    protected fun <X> remoteOperation(method: HttpMethod, query: Collection<Pair<String, Any>> = emptyList(), body: ()->String? = {null}, future: Promise<X>,  callback: (HttpClientRequest, HttpClientResponse)->Unit) {
        val path = tupleSpacePath + if (query.isEmpty()) "" else query.joinToString("&", "?"){
            "${it.first}=${URLEncoder.encode(it.second.toString(), "UTF-8")}"
        }

        val request = webClient.request(method, service.port, service.host, path)
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