package services.user;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import pro.iamgamer.routing.RouteOrchestrator;


/**
 * Created by sergey.kobets on 14.12.2015.
 */
public class PrivateUserPageTest extends AbstractVerticle {
    private MongoClient shared;
    private JsonObject databaseConfig;
    private Integer port;
    private RouteOrchestrator routeOrchestrator;

    @Override
    public void start() throws Exception {
        serviceInitialization();
        Router router = Router.router(vertx);
        router.get().handler(requestHandler -> {
            User user = requestHandler.user();
            requestHandler.response().end(user.principal().encodePrettily());
        });
        routeOrchestrator.mountRequiresAuthorizationSubRouter("/user", router);

        vertx.createHttpServer().requestHandler(routeOrchestrator::accept).listen(port);
    }

    private void serviceInitialization() {
        JsonObject config = context.config();
        databaseConfig = config.getJsonObject("databaseConfig");
        port = config.getJsonObject("httServerConfig").getInteger("port");
        shared = MongoClient.createShared(vertx, databaseConfig);
        routeOrchestrator = RouteOrchestrator.getInstance(vertx, "/api");
    }

    @Override
    public void stop() throws Exception {
        shared.close();
    }
}
