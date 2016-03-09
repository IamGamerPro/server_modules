package services.register;

import client.OrientClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import pro.iamgamer.routing.RouteOrchestrator;
import services.configuration.RouteOrchestratorRuleImp;

/**
 * Created by Sergey Kobets on 27.02.2016.
 */
public class RegisterVerticle extends AbstractVerticle {
    private OrientClient databaseClient;

    @Override
    public void start() throws Exception {
        RouteOrchestrator instance = RouteOrchestrator.getInstance(vertx, "/api", new RouteOrchestratorRuleImp());
        databaseClient = OrientClient.createShared(vertx, new JsonObject().put("url", "plocal:/test"), "registerPool");
        RegisterService registerService = new RegisterService(databaseClient);
        Router router = Router.router(vertx);

        router.get("/user-exists").handler(registerService::isUniqueLogin);
        router.get("/email-exists").handler(registerService::isUniqueEmail);
        router.post().handler(registerService::register);
        instance.mountSubRouter("/register/v1/", router);
        vertx.createHttpServer().requestHandler(instance::accept).listen(8080);
    }

    @Override
    public void stop() throws Exception {
        databaseClient.close();
    }
}
