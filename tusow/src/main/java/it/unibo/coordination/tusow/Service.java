package it.unibo.coordination.tusow;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import it.unibo.coordination.tusow.routes.Path;
import it.unibo.coordination.tusow.routes.TupleSpacesPath;

public class Service extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
    
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
    		return 8080;
    	}
    }

    private void attach(Router router, Path path) {
        path.attach(router);
    }

    public static void main(String... args) {
        final Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(Service.class.getName());
    }
}