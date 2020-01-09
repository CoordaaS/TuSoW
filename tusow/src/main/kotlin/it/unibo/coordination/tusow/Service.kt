package it.unibo.coordination.tusow;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import it.unibo.coordination.tusow.routes.Path;
import it.unibo.coordination.tusow.routes.TupleSpacesPath;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Service extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
    private static final int DEFAULT_PORT = 8080;

    private final CompletableFuture<Service> deployment = new CompletableFuture<>();
    private final CompletableFuture<Void> termination  = new CompletableFuture<>();
    private Router router;
    private HttpServer server;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        router = Router.router(vertx);
    }

    @Override
    public void start(Future<Void> startFuture) {
        Json.mapper.registerModule(new JavaTimeModule());

        attach(router, new TupleSpacesPath("1", "tuple-spaces"));

        server = getVertx().createHttpServer()
                .requestHandler(router::accept)
                .listen(getPort(), x -> {
                    if (x.succeeded()) {
                        LOGGER.info("Service listening on port: {}", "" + getPort());
                        startFuture.complete();
                        deployment.complete(this);
                    } else {
                        LOGGER.info("Failure in starting the server on port {}", "" + getPort());
                        startFuture.fail(x.cause());
                        deployment.completeExceptionally(x.cause());
                    }
                });

    }

    public Service awaitDeployment() throws ExecutionException, InterruptedException {
        return deployment.get();
    }

    @Override
    public void start() {
        start(Future.future());
    }

    @Override
    public void stop() {
        stop(Future.future());
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        server.close(x -> {
            if (x.succeeded()) {
                LOGGER.info("Service is not listening anymore");
                stopFuture.complete();
                termination.complete(null);
            } else {
                LOGGER.info("Failure in shutting down the service");
                stopFuture.fail(x.cause());
                termination.completeExceptionally(x.cause());
            }
        });
    }


    public void awaitTermination() throws ExecutionException, InterruptedException {
        termination.get();
    }

    private int getPort() {
    	final JsonObject config = context.config();
    	if (config != null && config.containsKey("port")) {
    		return config.getInteger("port");
    	} else {
            return DEFAULT_PORT;
    	}
    }

    private void attach(Router router, Path path) {
        path.attach(router);
    }

    public static void main(String... args) throws InterruptedException {
        start(args);
    }

    public static Service start(String... args) {
        try {
            final Vertx vertx = Vertx.vertx();
            final JsonObject config = parserArgs(args);
            final Service service = new Service();
            vertx.deployVerticle(service, new DeploymentOptions(config));
            return service;
        } catch (HelpRequestedException e) {
            e.printHelp();
            return null;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static JsonObject parserArgs(String... args) throws ParseException, HelpRequestedException {
        Options opts = new Options();
        opts.addOption("p", "port", true, String.format("the service port (default %d)", DEFAULT_PORT));
        opts.addOption("h", "help", false, "shows this help message");

        CommandLineParser parser = new DefaultParser();
        CommandLine parsedArgs = parser.parse(opts, args);

        if (parsedArgs.hasOption("h")) {
            throw new HelpRequestedException(opts);
        }

        JsonObject obj = new JsonObject();
        for (Option option : parsedArgs.getOptions()) {
            obj.put(option.getLongOpt(), Integer.parseInt(option.getValue()));
        }
        return new JsonObject().put("config", obj);
    }

    private static class HelpRequestedException extends Exception {
        private final Options options;

        public HelpRequestedException(Options options) {
            this.options = options;
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }

        public void printHelp() {
            new HelpFormatter().printHelp("tusow", options);
        }
    }
}