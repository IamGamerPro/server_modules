package services.register;

import client.OrientClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import pro.iamgamer.config.Configuration;
import pro.iamgamer.routing.RouteOrchestrator;


/**
 * Created by Sergey Kobets on 27.02.2016.
 */
public class RegisterVerticle extends AbstractVerticle {
    private OrientClient databaseClient;

    @Override
    public void start() throws Exception {
        RouteOrchestrator instance = RouteOrchestrator.getInstance(vertx, "/api");
        Configuration baseConfiguration = Configuration.getBaseConfiguration(vertx);
        databaseClient = OrientClient.createShared(vertx, baseConfiguration.getDatabaseConfig(), "registerPool");
        RegisterService registerService = new RegisterService(databaseClient);
        Router router = Router.router(vertx);
        router.get("/user-exists").blockingHandler(registerService::isUniqueLogin, false);
        router.get("/email-exists").blockingHandler(registerService::isUniqueEmail, false);
        router.post().blockingHandler(registerService::register, false);
        instance.mountPublicSubRouter("/register/v1/", router);
        vertx.createHttpServer().requestHandler(instance::accept).listen(baseConfiguration.getHttpServerPort());
    }

    @Override
    public void stop() throws Exception {
        databaseClient.close();
    }
}
