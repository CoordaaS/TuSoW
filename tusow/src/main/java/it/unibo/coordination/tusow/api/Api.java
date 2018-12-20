package it.unibo.coordination.tusow.api;

import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

public interface Api {

    RoutingContext getRoutingContext();

    default String getAuthorizationHeader() {
        return getRoutingContext().request().getHeader(HttpHeaders.AUTHORIZATION);
    }

    default String getPath() {
        final String path = getRoutingContext().request().path();
        final int queryIndex = path.indexOf('?');
        return path.substring(0, queryIndex > 0 ? queryIndex : path.length());
    }

    default String getPath(String subPath) {
        return getPath() + "/" + Objects.requireNonNull(subPath);
    }
}
