package services;

import auth.OrientDBAuthProvider;
import client.OrientClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import pro.iamgamer.routing.RouteOrchestrator;
import services.configuration.RouteOrchestratorRuleImp;


/**
 * Created by sergey.kobets on 14.12.2015.
 */
public class LoginVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        RouteOrchestrator instance = RouteOrchestrator.getInstance(vertx, "/api", new RouteOrchestratorRuleImp());
        JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                .put("path", "keystore.jceks")
                .put("type", "jceks")
                .put("password", "avt564180"));
        OrientClient databaseClient = OrientClient.createShared(vertx, new JsonObject().put("url", "plocal:/test"), "loginPool");
        final OrientDBAuthProvider orientDBAuthProvider = OrientDBAuthProvider.create(databaseClient);
        JWTAuth provider = JWTAuth.create(vertx, config);
        Router router = Router.router(vertx);
        router.route().handler(JWTAuthHandler.create(provider, "/login"));
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(UserSessionHandler.create(provider));
        instance.mountSubRouter("/private", router);

        router.post("/login").handler(event -> {
            final JsonObject bodyAsJson = event.getBodyAsJson();
            try {
                orientDBAuthProvider.authenticate(bodyAsJson, authEvent -> {
                    if (authEvent.succeeded()) {
                        final String chunk =
                                provider.generateToken(new JsonObject(), new JWTOptions().setExpiresInSeconds(360L));
                        event.response().end(chunk);
                    } else {
                        event.response().setStatusCode(403).end();
                    }
                });
            } catch (Exception e) {
                event.response().setStatusCode(403).end();
            }
        });
        vertx.createHttpServer().requestHandler(instance::accept).listen(8080);
    }
}
