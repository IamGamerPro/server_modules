package pro.iamgamer.routing;

import com.google.common.collect.Sets;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import pro.iamgamer.routing.csrf.PersistCSRFHandler;
import pro.iamgamer.routing.imp.IamGamerRule;

import java.util.Objects;

/**
 * Created by Sergey Kobets on 04.03.2016.
 */
public class RouteOrchestratorImp implements RouteOrchestrator {
    private final static String SHARED = "iamgamer.shared.Routers";
    private final BaseRouterHolder holder;
    private final RouteOrchestratorRule routeOrchestratorRule;
    private final Router baseRouter;

    public RouteOrchestratorImp(Vertx vertx, String webroot) {
        this(vertx, webroot, new IamGamerRule());
    }

    public RouteOrchestratorImp(Vertx vertx, String webroot, RouteOrchestratorRule routeOrchestratorRule) {
        this.holder = lookupHolder(vertx, webroot);
        this.routeOrchestratorRule = routeOrchestratorRule;
        this.baseRouter = holder.router(routeOrchestratorRule);
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

        private synchronized Router router(RouteOrchestratorRule routeOrchestratorRule) {
            Objects.nonNull(routeOrchestratorRule);
            if (router == null) {
                router = Router.router(vertx);
                router.route().handler(BodyHandler.create());
                router.route().handler(CookieHandler.create());
                SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx));
                sessionHandler.setCookieHttpOnlyFlag(true);
                sessionHandler.setCookieSecureFlag(true);
                sessionHandler.setSessionCookieName("JSESSIONID");
                router.route().handler(sessionHandler);
                CorsHandler cors = CorsHandler
                                        .create("*")
                                        .allowedMethods(Sets.newHashSet(HttpMethod.OPTIONS, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.GET, HttpMethod.PUT))
                                        .allowedHeaders(Sets.newHashSet("X-JWT-TOKEN", "X-XSRF-TOKEN", "Authorization"))
                                        .allowedHeader("X-XSRF-TOKEN")
                                        .allowCredentials(true);
                router.route().handler(cors);
                JsonObject keyStoreConfig = vertx.getOrCreateContext().config().getJsonObject("keyStoreConfig");
                JWTAuth jwtAuth = JWTAuth.create(vertx, keyStoreConfig);
                JWTAuthHandler jwtAuthHandler = JWTAuthHandler.create(jwtAuth);
                PersistCSRFHandler csrfHandler = PersistCSRFHandler.create("qwerty1234");
                router.route(routeOrchestratorRule.privateUrlPatch() + "/*").handler(jwtAuthHandler);
                router.route(routeOrchestratorRule.privateUrlPatch() + "/*").handler(csrfHandler);
            }
            return router;

        }
    }

    @Override
    public Router mountPublicSubRouter(String mountPoint, Router subRouter) {
        if (mountPoint.endsWith("*")) {
            throw new IllegalArgumentException("Don't include * when mounting subrouter");
        }
        if (mountPoint.contains(":")) {
            throw new IllegalArgumentException("Can't use patterns in subrouter mounts");
        }
        return baseRouter.mountSubRouter(mountPoint, subRouter);
    }

    @Override
    public Router mountRequiresAuthorizationSubRouter(String mountPoint, Router subRouter) {
        return mountPublicSubRouter(routeOrchestratorRule.privateUrlPatch() + mountPoint, subRouter);
    }


    @Override
    public void accept(HttpServerRequest request) {
        baseRouter.accept(request);
    }
}
