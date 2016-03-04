package pro.iamgamer.routing;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;

import java.util.Objects;

/**
 * Created by Sergey Kobets on 04.03.2016.
 */
public interface RouteOrchestrator {

    static RouteOrchestrator getInstance(Vertx vertx, String webroot) {
        Objects.nonNull(vertx);
        Objects.nonNull(webroot);
        return new RouteOrchestratorImp(vertx, webroot);
    }

    static RouteOrchestrator getInstance(Vertx vertx, String webroot, RouteOrchestratorRule routeOrchestratorRule) {
        Objects.nonNull(vertx);
        Objects.nonNull(webroot);
        Objects.nonNull(routeOrchestratorRule);
        return new RouteOrchestratorImp(vertx, webroot, routeOrchestratorRule);
    }

    Router mountSubRouter(String mountPoint, Router subRouter);

    void accept(HttpServerRequest request);
}
