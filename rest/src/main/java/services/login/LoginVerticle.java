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
    private JsonObject keyStoreConfig;
    private JsonObject databaseConfig;
    private JsonObject mongoDbAuthConfig;
    private IamGamerRule iamGamerRule;
    private MongoAuth mongoAuth;
    private JWTAuth jwtAuth;
    private JWTAuthHandler jwtAuthHandler;
    private String privateUrls;
    private String loginUrl;
    private PersistCSRFHandler csrfHandler;
    private Integer port;
    private MongoClient shared;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        routeOrchestrator = RouteOrchestrator.getInstance(vertx, "/api");
        loadConfiguration(context.config());
    }

    @Override
    public void start() throws Exception {
        serviceInitialization();
        Router router = routeOrchestrator.getBaseRouter();
        router.route(privateUrls).handler(jwtAuthHandler);
        router.route(privateUrls).handler(csrfHandler);
        router.post(loginUrl).handler(requestHandler -> {
            JsonObject authParams = requestHandler.getBodyAsJson();
            mongoAuth.authenticate(authParams, event -> {
                if (event.succeeded()) {
                    String value = jwtAuth.generateToken(new JsonObject(), new JWTOptions()
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
        vertx.createHttpServer().requestHandler(routeOrchestrator::accept).listen(port);
    }

    private void serviceInitialization() {
        shared = MongoClient.createShared(vertx, databaseConfig);
        mongoAuth = MongoAuth.create(shared, mongoDbAuthConfig);
        mongoAuth.setUsernameCredentialField(mongoAuth.getUsernameField());
        mongoAuth.setPasswordCredentialField(mongoAuth.getPasswordField());
        jwtAuth = JWTAuth.create(vertx, keyStoreConfig);
        jwtAuthHandler = JWTAuthHandler.create(jwtAuth);
        csrfHandler = PersistCSRFHandler.create("qwerty1234");
    }

    private void loadConfiguration(JsonObject config) {
        keyStoreConfig = config.getJsonObject("keyStoreConfig");
        databaseConfig = config.getJsonObject("databaseConfig");
        mongoDbAuthConfig = config.getJsonObject("mongoDbAuthConfig");
        port = config.getJsonObject("httServerConfig").getInteger("port");
        iamGamerRule = new IamGamerRule();
        privateUrls = iamGamerRule.privateUrlPatch() + "/*";
        loginUrl = iamGamerRule.loginUrl();
    }

    @Override
    public void stop() throws Exception {
        shared.close();
    }
}
