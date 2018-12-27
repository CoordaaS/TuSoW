package it.unibo.coordination.tusow.routes;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import it.unibo.coordination.tusow.exceptions.BadContentError;
import it.unibo.coordination.tusow.exceptions.HttpError;
import it.unibo.coordination.tusow.presentation.MIMETypes;
import it.unibo.coordination.tusow.presentation.Marshaller;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class Path {

    private static final Logger LOGGER = LoggerFactory.getLogger(Path.class);

	private String path;
    private Router router;
    private String parentPath = "";

	public Path(String subPath) {
		this.path = Objects.requireNonNull(subPath);

	}

	protected abstract void setupRoutes();
	
	protected Route addRoute(HttpMethod method, String path, Handler<RoutingContext> handler) {
	    LOGGER.info("Add route: {0} {1}", method, getPath() + path);
		return router.route(method, getPath() + path)
	        .handler(LoggerHandler.create())
	    	.handler(BodyHandler.create())
	    	.handler(handler)
	    	.handler(ErrorHandler.create());
    }

	protected Route addRoute(HttpMethod method, Handler<RoutingContext> handler) {
		return addRoute(method, "", handler);
	}

	protected String getPath() {
		return parentPath + path;
	}

	protected String getSubPath(String subResource) {
		return getPath() + "/" + subResource;
	}

    protected <X> Handler<AsyncResult<X>> responseHandler(RoutingContext routingContext, Function<MIMETypes, Marshaller<X>> marshaller) {
	    return responseHandler(routingContext, marshaller, Function.identity());
    }

	protected <X> Handler<AsyncResult<Collection<? extends X>>> responseHandlerWithManyContents(RoutingContext routingContext, Function<MIMETypes, Marshaller<X>> marshaller) {
		return responseHandlerWithManyContents(routingContext, marshaller, Function.identity());
	}

	protected <X> Handler<AsyncResult<X>> responseHandler(RoutingContext routingContext, Function<MIMETypes, Marshaller<X>> marshaller, Function<X, X> cleaner) {
		return x -> {
			if (x.failed() && x.cause() instanceof HttpError) {
				final HttpError exception = (HttpError) x.cause();
				routingContext.response()
						.setStatusCode(exception.getStatusCode())
						.end(exception.getMessage());
			} else if (x.failed()) {
				routingContext.response()
						.setStatusCode(500)
						.end("Internal Server Error");

			} else {
			    try {
                    final X cleanResult = cleaner.apply(x.result());
                    final MIMETypes mimeType = MIMETypes.parse(routingContext.getAcceptableContentType());
					final String result;

                    if (cleanResult instanceof Collection) {
                    	throw new UnsupportedOperationException();
					}

					result = marshaller.apply(mimeType).toString(cleanResult);

                    routingContext.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, mimeType.toString())
                            .setStatusCode(200)
                            .end(result);
                } catch (HttpError e)  {
					routingContext.response()
							.setStatusCode(e.getStatusCode())
							.end(e.getMessage());
				} catch (Exception e) {
                    routingContext.response()
                            .setStatusCode(500)
                            .end("Internal Server Error");
                }
			}
		};
	}

	protected <X> Handler<AsyncResult<Collection<? extends X>>> responseHandlerWithManyContents(RoutingContext routingContext, Function<MIMETypes, Marshaller<X>> marshaller, Function<Collection<? extends X>, Collection<? extends X>> cleaner) {
		return x -> {
			if (x.failed() && x.cause() instanceof HttpError) {
				final HttpError exception = (HttpError) x.cause();
				routingContext.response()
						.setStatusCode(exception.getStatusCode())
						.end(exception.getMessage());
			} else if (x.failed()) {
				routingContext.response()
						.setStatusCode(500)
						.end("Internal Server Error");

			} else {
				try {
					final Collection<? extends X> cleanResult = cleaner.apply(x.result());
					final MIMETypes mimeType = MIMETypes.parse(routingContext.getAcceptableContentType());
					final String result;

					result = marshaller.apply(mimeType).toString(cleanResult);

					routingContext.response()
							.putHeader(HttpHeaders.CONTENT_TYPE, mimeType.toString())
							.setStatusCode(cleanResult.isEmpty() ? 204 : 200)
							.end(result);
				} catch (HttpError e)  {
					routingContext.response()
							.setStatusCode(e.getStatusCode())
							.end(e.getMessage());
				} catch (Exception e) {
					routingContext.response()
							.setStatusCode(500)
							.end("Internal Server Error");
				}
			}
		};
	}

	protected Handler<AsyncResult<Void>> responseHandlerWithNoContent(RoutingContext routingContext) {
		return x -> {
			if (x.failed() && x.cause() instanceof HttpError) {
				final HttpError exception = (HttpError) x.cause();
				routingContext.response()
						.setStatusCode(exception.getStatusCode())
						.end(exception.getMessage());
			} else if (x.failed()) {
				routingContext.response()
						.setStatusCode(500)
						.end("Internal Server Error");

			} else {
				routingContext.response()
						.setStatusCode(204)
						.end();
			}
		};
	}

	public void attach(Router router) {
        this.router = Objects.requireNonNull(router);
        setupRoutes();
    }

    public Path append(Path route) {
        route.parentPath = path;
        route.attach(router);
        return route;
    }

    public Path append(String subPath, Path route) {
        route.parentPath = parentPath + path + subPath;
        route.attach(router);
        return route;
    }

    protected static void requireAllAreNull(Object x, Object... xs) {
        if (Stream.concat(Stream.of(x), Stream.of(xs)).anyMatch(Objects::nonNull)) {
            throw new BadContentError();
        }
    }

    protected static void requireAllAreNullOrEmpty(Collection<?> x, Collection<?>... xs) {
        if (Stream.concat(Stream.of(x), Stream.of(xs))
                .filter(Objects::nonNull).anyMatch(it -> it.size() > 0)) {
            throw new BadContentError();
        }
    }

    protected static void requireNoneIsNull(Object x, Object... xs) {
        if (Stream.concat(Stream.of(x), Stream.of(xs)).anyMatch(Objects::isNull)) {
            throw new BadContentError();
        }
    }

    protected static void requireSomeIsNonNull(Object x, Object... xs) {
        if (!Stream.concat(Stream.of(x), Stream.of(xs)).anyMatch(Objects::nonNull)) {
            throw new BadContentError();
        }
    }


}
