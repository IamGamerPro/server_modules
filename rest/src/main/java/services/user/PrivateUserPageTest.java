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

    @Override
    public void start() throws Exception {
        JsonObject config = context.config();
        JsonObject databaseConfig = config.getJsonObject("databaseConfig");
        Integer port = config.getJsonObject("httServerConfig").getInteger("port");
        shared = MongoClient.createShared(vertx, databaseConfig);
        RouteOrchestrator instance = RouteOrchestrator.getInstance(vertx, "/api");
        Router router = Router.router(vertx);
        router.get().handler(requestHandler -> {
            User user = requestHandler.user();
            requestHandler.response().end(user.principal().encodePrettily());
        });
        instance.mountRequiresAuthorizationSubRouter("/user", router);

        vertx.createHttpServer().requestHandler(instance::accept).listen();
    }

    @Override
    public void stop() throws Exception {
        shared.close();
    }
}
