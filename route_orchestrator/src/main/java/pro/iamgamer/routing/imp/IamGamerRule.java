package pro.iamgamer.routing.imp;

import auth.OrientDBAuthProvider;
import client.OrientClient;
import com.google.common.collect.Sets;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import pro.iamgamer.routing.RouteOrchestratorRule;
import pro.iamgamer.routing.csrf.PersistCSRFHandler;

import java.util.Set;

/**
 * Created by Sergey Kobets on 09.03.2016.
 */
public class IamGamerRule implements RouteOrchestratorRule {
    private static final Set<Handler<RoutingContext>> handlers =
            Sets.newHashSet(BodyHandler.create(), CorsHandler.create("*"));

    @Override
    public Set<Handler<RoutingContext>> baseHandlers() {
        return handlers;
    }

    @Override
    public void buildAuthHandler(Router router, Vertx vertx) {
        JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                .put("path", "keystore.jceks")
                .put("type", "jceks")
                .put("password", "avt564180"));
        OrientClient databaseClient = OrientClient.createShared(vertx, new JsonObject().put("url", "plocal:/test"), "loginPool");
        final OrientDBAuthProvider orientDBAuthProvider = OrientDBAuthProvider.create(databaseClient);
        JWTAuth provider = JWTAuth.create(vertx, config);
        JWTAuthHandler jwtAuthHandler = JWTAuthHandler.create(provider, "/login");
        String privatePaths = this.privateUrlPatch() + "/*";
        router.route(privatePaths).handler(jwtAuthHandler);
        PersistCSRFHandler csrfHandler = PersistCSRFHandler.create("qwerty1234");
        router.route(privatePaths).handler(csrfHandler);
        router.post("/login").handler(requestHandler -> {
            JsonObject authParams = requestHandler.getBodyAsJson();
            orientDBAuthProvider.authenticate(authParams, event -> {
                if (event.succeeded()) {
                    String s = csrfHandler.generateToken();

                    requestHandler.response()
                            .putHeader(csrfHandler.getHeaderName(), s)
                            .putHeader("x-jwt-token", provider.generateToken(new JsonObject(), new JWTOptions()
                                    .setExpiresInMinutes(10080L)))
                            .end();
                }
                requestHandler.fail(403);
            });
        });
    }
}
