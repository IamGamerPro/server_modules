import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;

/**
 * Created by Sergey Kobets on 04.03.2016.
 */
public interface RouteOrchestrator {

    static RouteOrchestrator getInstance(Vertx vertx, String webroot) {
        return new RouteOrchestratorImp(vertx, webroot);
    }

    Router mountSubRouter(String mountPoint, Router subRouter);

    void accept(HttpServerRequest request);
}
