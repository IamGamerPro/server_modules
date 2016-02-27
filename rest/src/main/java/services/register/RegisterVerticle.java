package services.register;

import client.OrientClient;
import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sync.SyncVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;

import static io.vertx.ext.sync.Sync.fiberHandler;

/**
 * Created by Sergey Kobets on 27.02.2016.
 */
public class RegisterVerticle extends SyncVerticle {
    private OrientClient databaseClient;

    @Override
    @Suspendable
    public void start() throws Exception {
        databaseClient = OrientClient.createShared(vertx, new JsonObject().put("url", "plocal:/test"), "registerPool");
        RegisterService registerService = new RegisterService(databaseClient);
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*"));
        router.route().handler(ResponseTimeHandler.create());
        router.route().handler(LoggerHandler.create());

        router.get("/api/private/v1/user-exists").handler(fiberHandler(requestHandler -> {
                    String value = requestHandler.request().getParam("value");
                    boolean uniqueLogin = registerService.isUniqueLogin(value);
                    if (uniqueLogin) {
                        requestHandler.response().end();
                    } else {
                        // LOL :D
                        requestHandler.response().setStatusCode(418).end();
                    }
                }
        ));
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    @Override
    public void stop() throws Exception {
        databaseClient.close();
    }
}
