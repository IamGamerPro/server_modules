package services;

import auth.OrientDBAuthProvider;
import client.OrientClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;


/**
 * Created by sergey.kobets on 14.12.2015.
 */
public abstract class LoginVerticle extends AbstractVerticle {

    protected abstract void concrete(Router router);

    @Override
    public void start() throws Exception {
        JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                .put("path", "keystore.jceks")
                .put("type", "jceks")
                .put("password", "avt564180"));
        OrientClient databaseClient = OrientClient.createShared(vertx, new JsonObject().put("url", "plocal:/test"), "loginPool");
        final OrientDBAuthProvider orientDBAuthProvider = OrientDBAuthProvider.create(databaseClient);
        JWTAuth provider = JWTAuth.create(vertx, config);
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*"));
        router.route().handler(JWTAuthHandler.create(provider, "/api/private/v1/login"));
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(UserSessionHandler.create(provider));

        router.post("/api/private/v1/login").handler(event -> {
            final JsonObject bodyAsJson = event.getBodyAsJson();
            try {
                orientDBAuthProvider.authenticate(bodyAsJson, authEvent -> {
                    if (authEvent.succeeded()) {
                        final String chunk =
                                provider.generateToken(new JsonObject(), new JWTOptions().setExpiresInSeconds(360L));
                        final Session session = event.session();
                        session.put("currentToken", chunk);
                        event.response().end(chunk);
                    } else {
                        event.response().setStatusCode(403).end();
                    }
                });
            } catch (Exception e) {
                event.response().setStatusCode(403).end();
            }
        });
        concrete(router);
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}
