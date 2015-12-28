package services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import pro.iamgamer.core.database.dao.AuthenticationDao;
import pro.iamgamer.core.model.User;

import javax.inject.Inject;


/**
 * Created by sergey.kobets on 14.12.2015.
 */
public abstract class CoreVerticleImp extends AbstractVerticle {
    @Inject
    AuthenticationDao authenticationDao;

    protected Router router;
    protected abstract void concrete();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                .put("path", "keystore.jceks")
                .put("type", "jceks")
                .put("password", "avt564180"));

        JWTAuth provider = JWTAuth.create(vertx, config);
        router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*"));
        router.route().handler(JWTAuthHandler.create(provider, "/api/login"));
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(UserSessionHandler.create(provider));

        router.post("/api/login").handler(event -> {
            final JsonObject bodyAsJson = event.getBodyAsJson();
            final String login = bodyAsJson.getString("login");
            final String password = bodyAsJson.getString("password");
            try {
                final User login1 = authenticationDao.login(login, password);
                final String chunk =
                        provider.generateToken(new JsonObject(), new JWTOptions().setExpiresInSeconds(360));
                final Session session = event.session();
                session.put("currentToken", chunk);
                event.response().end(chunk);
            } catch (Exception e) {
                event.response().setStatusCode(403).end();
            }
        });
        concrete();

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

    }
}
