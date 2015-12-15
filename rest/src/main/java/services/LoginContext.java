package services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import pro.iamgamer.core.database.dao.AuthenticationDao;
import pro.iamgamer.core.model.User;

import javax.inject.Inject;


/**
 * Created by sergey.kobets on 14.12.2015.
 */
public class LoginContext extends AbstractVerticle {
    @Inject
    AuthenticationDao authenticationDao;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                .put("path", "keystore.jceks")
                .put("type", "jceks")
                .put("password", "avt564180"));

        JWTAuth provider = JWTAuth.create(vertx, config);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*"));
        router.route().handler(JWTAuthHandler.create(provider, "/api/login"));
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(UserSessionHandler.create(provider));

        router.post("/api/login").handler(event -> {
            final JsonObject bodyAsJson = event.getBodyAsJson();
            System.out.println(bodyAsJson);
            final String login = bodyAsJson.getString("login");
            final String password = bodyAsJson.getString("password");
            try {
                final User login1 = authenticationDao.login(login, password);
                event.response().end(provider.generateToken(new JsonObject(), new JWTOptions()
                        .setExpiresInSeconds(60)));
            } catch (Exception e) {
                event.response().setStatusCode(403).end();
            }
        });
        router.route("/api/*").handler(RoutingContext::next);
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

    }
}
