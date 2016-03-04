import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Sergey Kobets on 04.03.2016.
 */
public class RouteOrchestratorImp implements RouteOrchestrator {
    private final static String SHARED = "iamgamer.shared.Routers";
    private final Vertx vertx;
    private final BaseRouterHolder holder;
    private final Router baseRouter;

    public RouteOrchestratorImp(Vertx vertx, String webroot, final Handler<RoutingContext>[] rootHandlers) {
        this.vertx = vertx;
        this.holder = lookupHolder(vertx, webroot);
        this.baseRouter = holder.router(rootHandlers);
    }

    private BaseRouterHolder lookupHolder(Vertx vertx, String webroot) {
        synchronized (vertx) {
            LocalMap<String, BaseRouterHolder> cashedPool =
                    vertx.sharedData().getLocalMap(SHARED);
            BaseRouterHolder holder = cashedPool.get(webroot);
            if (holder == null) {
                holder = new BaseRouterHolder(vertx);
                cashedPool.put(webroot, holder);
            }
            return holder;
        }
    }

    private static class BaseRouterHolder implements Shareable {
        private final Vertx vertx;
        private Router router;

        private BaseRouterHolder(Vertx vertx) {
            this.vertx = vertx;
        }

        private synchronized Router router(Handler<RoutingContext>[] rootHandlers) {
            if (router == null) {
                router = Router.router(vertx);
            }
            for (Handler<RoutingContext> rootHandler : rootHandlers) {
                router.route().handler(rootHandler);
            }
            return router;

        }

    }

    @Override
    public Router mountSubRouter(String mountPoint, Router subRouter) {
        return baseRouter.mountSubRouter(mountPoint, subRouter);
    }

    @Override
    public void accept(HttpServerRequest request) {
        baseRouter.accept(request);
    }
}
