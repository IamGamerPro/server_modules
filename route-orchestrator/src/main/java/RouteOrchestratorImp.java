import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;
import io.vertx.ext.web.Router;

/**
 * Created by Sergey Kobets on 04.03.2016.
 */
public class RouteOrchestratorImp implements RouteOrchestrator {
    private final Vertx vertx;
    private final BaseRouterHolder holder;
    private final Router baseRouter;

    public RouteOrchestratorImp(Vertx vertx, String webroot) {
        this.vertx = vertx;
        this.holder = lookup(vertx, webroot);
        this.baseRouter = holder.router();
    }

    private BaseRouterHolder lookup(Vertx vertx, String webroot) {
        synchronized (vertx) {
            LocalMap<String, BaseRouterHolder> cashedPool =
                    vertx.sharedData().getLocalMap("trololol");
            BaseRouterHolder baseRouter = cashedPool.get(webroot);
            if (baseRouter == null) {
                baseRouter = new BaseRouterHolder(vertx);
                cashedPool.put(webroot, baseRouter);
            }
            return baseRouter;
        }
    }

    private static class BaseRouterHolder implements Shareable {
        private final Vertx vertx;
        private Router baseRouter;

        private BaseRouterHolder(Vertx vertx) {
            this.vertx = vertx;
        }

        private synchronized Router router() {
            if (baseRouter == null) {
                baseRouter = Router.router(vertx);
            }
            return baseRouter;

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
