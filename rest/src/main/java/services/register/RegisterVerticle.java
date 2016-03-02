package services.register;

import client.OrientClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;

/**
 * Created by Sergey Kobets on 27.02.2016.
 */
public class RegisterVerticle extends AbstractVerticle {
    private OrientClient databaseClient;

    @Override
    public void start() throws Exception {
        databaseClient = OrientClient.createShared(vertx, new JsonObject().put("url", "plocal:/test"), "registerPool");
        RegisterService registerService = new RegisterService(databaseClient);
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*"));
        router.route().handler(ResponseTimeHandler.create());
        router.route().handler(LoggerHandler.create());
        router.get("/api/private/v1/user-exists").handler(registerService::isUniqueLogin);
        router.get("/api/private/v1/email-exists").handler(registerService::isUniqueEmail);
        router.post("/api/private/v1/register").handler(registerService::register);
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    @Override
    public void stop() throws Exception {
        databaseClient.close();
    }
}
