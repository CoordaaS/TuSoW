package it.unibo.coordination.tusow;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import it.unibo.coordination.tusow.routes.Path;
import it.unibo.coordination.tusow.routes.TupleSpacesPath;
import org.apache.commons.cli.*;

public class Service extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
    private static final int DEFAULT_PORT = 8080;

    private Router router;

    private HttpServer server;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        router = Router.router(vertx);
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Json.mapper.registerModule(new JavaTimeModule());

        attach(router, new TupleSpacesPath("1", "tuple-spaces"));
        
        server = getVertx().createHttpServer()
	        .requestHandler(router)
	        .listen(getPort(), x -> {
                LOGGER.info("Service listening on port: {0}", "" + getPort());
                startFuture.complete();
            });

    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        server.close(x -> {
            LOGGER.info("Service is not listening anymore");
            stopFuture.complete();
        });
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

    public static void main(String... args) throws ParseException {

        try {
            final Vertx vertx = Vertx.vertx();
            final JsonObject config = parserArgs(args);
            vertx.deployVerticle(Service.class.getName(), new DeploymentOptions(config));
        } catch (HelpRequestedException e) {
            e.printHelp();
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