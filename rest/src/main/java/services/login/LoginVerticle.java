package services.login;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;
import pro.iamgamer.auth.mongo.MongoAuth;
import pro.iamgamer.routing.RouteOrchestrator;
import pro.iamgamer.routing.csrf.PersistCSRFHandler;
import pro.iamgamer.routing.imp.IamGamerRule;

/**
 * Created by Sergey Kobets on 09.04.2016.
 */
public class LoginVerticle extends AbstractVerticle {
    private RouteOrchestrator routeOrchestrator;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        routeOrchestrator = RouteOrchestrator.getInstance(vertx, "/api");
    }

    @Override
    public void start() throws Exception {
        JsonObject config = context.config();
        JsonObject keyStoreConfig = config.getJsonObject("keyStoreConfig");
        JsonObject databaseConfig = config.getJsonObject("databaseConfig");
        JsonObject mongoDbAuthConfig = config.getJsonObject("mongoDbAuthConfig");
        IamGamerRule iamGamerRule = new IamGamerRule();
        MongoClient shared = MongoClient.createShared(vertx, databaseConfig);
        MongoAuth mongoAuth = MongoAuth.create(shared, mongoDbAuthConfig);
        mongoAuth.setUsernameCredentialField(mongoAuth.getUsernameField());
        mongoAuth.setPasswordCredentialField(mongoAuth.getPasswordField());
        JWTAuth provider = JWTAuth.create(vertx, keyStoreConfig);
        JWTAuthHandler jwtAuthHandler = JWTAuthHandler.create(provider);
        String privatePaths = iamGamerRule.privateUrlPatch() + "/*";
        Router router = routeOrchestrator.getBaseRouter();
        router.route(privatePaths).handler(jwtAuthHandler);
        PersistCSRFHandler csrfHandler = PersistCSRFHandler.create("qwerty1234");
        router.route(privatePaths).handler(csrfHandler);
        router.post(iamGamerRule.loginUrl()).handler(requestHandler -> {
            JsonObject authParams = requestHandler.getBodyAsJson();
            mongoAuth.authenticate(authParams, event -> {
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
        vertx.createHttpServer().requestHandler(routeOrchestrator::accept).listen(8080);
    }
}
