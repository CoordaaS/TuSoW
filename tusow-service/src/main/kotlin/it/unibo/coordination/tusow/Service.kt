package it.unibo.coordination.tusow

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vertx.core.*
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import it.unibo.coordination.tusow.routes.Path
import it.unibo.coordination.tusow.routes.TupleSpacesPath
import org.apache.commons.cli.*
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

@Suppress("DEPRECATION")
class Service : AbstractVerticle() {

    private val deployment = CompletableFuture<Service>()
    private val termination = CompletableFuture<Void?>()
    private var router: Router? = null
    private var server: HttpServer? = null

    override fun init(vertx: Vertx, context: Context) {
        super.init(vertx, context)
        router = Router.router(vertx)
    }

    override fun start(startFuture: Future<Void>) {
        Json.mapper.registerModule(JavaTimeModule())
        attach(router!!, TupleSpacesPath("1", "tuple-spaces"))
        server = getVertx().createHttpServer()
                .requestHandler { request: HttpServerRequest? -> router!!.accept(request) }
                .listen(port) { x: AsyncResult<HttpServer?> ->
                    if (x.succeeded()) {
                        LOGGER.info("Service listening on port: {}", "" + port)
                        startFuture.complete()
                        deployment.complete(this)
                    } else {
                        LOGGER.info("Failure in starting the server on port {}", "" + port)
                        startFuture.fail(x.cause())
                        deployment.completeExceptionally(x.cause())
                    }
                }
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun awaitDeployment(): Service {
        return deployment.get()
    }

    override fun start() {
        start(Future.future())
    }

    override fun stop() {
        stop(Future.future())
    }

    override fun stop(stopFuture: Future<Void>) {
        server!!.close { x: AsyncResult<Void?> ->
            if (x.succeeded()) {
                LOGGER.info("Service is not listening anymore")
                stopFuture.complete()
                termination.complete(null)
            } else {
                LOGGER.info("Failure in shutting down the service")
                stopFuture.fail(x.cause())
                termination.completeExceptionally(x.cause())
            }
        }
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun awaitTermination() {
        termination.get()
    }

    private val port: Int
        get() {
            val config = context.config()
            return if (config != null && config.containsKey("port")) {
                config.getInteger("port")
            } else {
                DEFAULT_PORT
            }
        }

    private fun attach(router: Router, path: Path) {
        path.attach(router)
    }

    private class HelpRequestedException(private val options: Options) : Exception() {
        @Synchronized
        override fun fillInStackTrace(): Throwable {
            return this
        }

        fun printHelp() {
            HelpFormatter().printHelp("tusow", options)
        }

    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Service::class.java)
        private const val DEFAULT_PORT = 8080

        @Throws(InterruptedException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            start(*args)
        }

        @JvmStatic
        fun start(vararg args: String): Service? {
            return try {
                val vertx = Vertx.vertx()
                val config = parserArgs(*args)
                val service = Service()
                vertx.deployVerticle(service, DeploymentOptions(config))
                service
            } catch (e: HelpRequestedException) {
                e.printHelp()
                null
            } catch (e: ParseException) {
                throw IllegalArgumentException(e)
            }
        }

        @Throws(ParseException::class, HelpRequestedException::class)
        private fun parserArgs(vararg args: String): JsonObject {
            val opts = Options()
            opts.addOption("p", "port", true, String.format("the service port (default %d)", DEFAULT_PORT))
            opts.addOption("h", "help", false, "shows this help message")
            val parser: CommandLineParser = DefaultParser()
            val parsedArgs = parser.parse(opts, args)
            if (parsedArgs.hasOption("h")) {
                throw HelpRequestedException(opts)
            }
            val obj = JsonObject()
            for (option in parsedArgs.options) {
                obj.put(option.longOpt, option.value.toInt())
            }
            return JsonObject().put("config", obj)
        }
    }
}