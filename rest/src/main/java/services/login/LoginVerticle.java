package services.login;

import auth.OrientDBAuthProvider;
import client.OrientClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;
import pro.iamgamer.config.Configuration;
import pro.iamgamer.routing.RouteOrchestrator;
import pro.iamgamer.routing.csrf.PersistCSRFHandler;
import pro.iamgamer.routing.imp.IamGamerRule;

/**
 * Created by Sergey Kobets on 09.04.2016.
 */
public class LoginVerticle extends AbstractVerticle {
    private Configuration configuration;
    private RouteOrchestrator routeOrchestrator;
    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        configuration = Configuration.getBaseConfiguration(vertx);
        routeOrchestrator = RouteOrchestrator.getInstance(vertx, "/api");
    }

    @Override
    public void start() throws Exception {
        JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                .put("path", "keystore.jceks")
                .put("type", "jceks")
                .put("password", "avt564180"));
        JsonObject databaseConfig = configuration.getDatabaseConfig()
                .put("max_pool_size", 50);
        IamGamerRule iamGamerRule = new IamGamerRule();
        OrientClient databaseClient = OrientClient.createShared(vertx, databaseConfig, "loginPool");
        final OrientDBAuthProvider orientDBAuthProvider = OrientDBAuthProvider.create(databaseClient);
        JWTAuth provider = JWTAuth.create(vertx, config);
        JWTAuthHandler jwtAuthHandler = JWTAuthHandler.create(provider);
        String privatePaths = iamGamerRule.privateUrlPatch() + "/*";
        Router router = routeOrchestrator.getBaseRouter();
        router.route(privatePaths).handler(jwtAuthHandler);
        PersistCSRFHandler csrfHandler = PersistCSRFHandler.create("qwerty1234");
        router.route(privatePaths).handler(csrfHandler);
        router.post(iamGamerRule.loginUrl()).handler(requestHandler -> {
            JsonObject authParams = requestHandler.getBodyAsJson();
            orientDBAuthProvider.authenticate(authParams, event -> {
                if (event.succeeded()) {
                    String value = provider.generateToken(new JsonObject(), new JWTOptions()
                            .setExpiresInMinutes(10080L));
                    String s = csrfHandler.generateToken();
                    requestHandler.response()
                            .putHeader(csrfHandler.getHeaderName(), s)
                            .putHeader("X-JWT-TOKEN", value)
                            .end();
                } else {
                    requestHandler.fail(403);
                }
            });
        });
        vertx.createHttpServer().requestHandler(routeOrchestrator::accept).listen(configuration.getHttpServerPort());
    }
}
