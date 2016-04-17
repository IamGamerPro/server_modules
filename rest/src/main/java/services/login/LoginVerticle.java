package services.login;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
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
        Router router = Router.router(vertx);
        router.post().handler(requestHandler -> {
            JsonObject authParams = requestHandler.getBodyAsJson();
            final Integer authenticationMode = authParams.getInteger("authenticationMode");
            mongoAuth.authenticate(authParams, event -> {
                if (event.succeeded()) {
                    switch (authenticationMode) {
                        case 0:
                            String value = jwtAuth.generateToken(new JsonObject().put("sub", authParams.getValue("login")), new JWTOptions()
                                    .setExpiresInMinutes(10080L));
                            String s = csrfHandler.generateToken();
                            requestHandler.response()
                                    .putHeader(csrfHandler.getHeaderName(), s)
                                    .putHeader("X-JWT-TOKEN", value)
                                    .end();
                            break;
                        case 1:
                            requestHandler.response().end();
                            break;
                        default:
                            requestHandler.response().end();
                    }
                } else {
                    requestHandler.fail(403);
                }
            });
        });
        routeOrchestrator.mountPublicSubRouter(loginUrl, router);
        vertx.createHttpServer().requestHandler(routeOrchestrator::accept).listen(port);
    }

    private void serviceInitialization() {
        shared = MongoClient.createShared(vertx, databaseConfig);
        mongoAuth = MongoAuth.create(shared, mongoDbAuthConfig);
        mongoAuth.setUsernameCredentialField(mongoAuth.getUsernameField());
        mongoAuth.setPasswordCredentialField(mongoAuth.getPasswordField());
        jwtAuth = JWTAuth.create(vertx, keyStoreConfig);
        csrfHandler = PersistCSRFHandler.create("qwerty1234");
    }

    private void loadConfiguration(JsonObject config) {
        keyStoreConfig = config.getJsonObject("keyStoreConfig");
        databaseConfig = config.getJsonObject("databaseConfig");
        mongoDbAuthConfig = config.getJsonObject("mongoDbAuthConfig");
        port = config.getJsonObject("httServerConfig").getInteger("port");
        iamGamerRule = new IamGamerRule();
        loginUrl = iamGamerRule.loginUrl();
    }

    @Override
    public void stop() throws Exception {
        shared.close();
    }
}
