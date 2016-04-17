package services.mail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.ext.web.Router;
import pro.iamgamer.routing.RouteOrchestrator;

/**
 * Created by Sergey Kobets on 16.04.2016.
 */
public class ConfirmationCallbackVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        RouteOrchestrator instance = RouteOrchestrator.getInstance(vertx, "/api");
        Router router = Router.router(vertx);
        JsonObject config = context.config();
        JsonObject databaseConfig = config.getJsonObject("databaseConfig");
        Integer port = config.getJsonObject("httServerConfig").getInteger("port");
        MongoClient mongoClient = MongoClient.createShared(vertx, databaseConfig, "callbacks");
        router.get("/:callbackId").handler(routingContext -> {
            String callbackId = routingContext.request().getParam("callbackId");
            mongoClient.findOne("callbacks", new JsonObject().put("_id", callbackId), null, result -> {
                if (result.succeeded()) {
                    JsonObject callback = result.result();
                    String type = callback.getString("type");
                    switch (type) {
                        case "mailValidation":
                            String user_id = callback.getString("user_id");
                            JsonObject selector = new JsonObject()
                                    .put("_id", new JsonObject().put("$oid", user_id))
                                    .put("emails.mail", callback.getString("email"));
                            JsonObject update = new JsonObject()
                                    .put("$set", new JsonObject().put("emails.$.checked", true)
                                    );
                            mongoClient.updateWithOptions("users", selector, update, new UpdateOptions().setWriteOption(WriteOption.ACKNOWLEDGED), r -> {
                                if (r.succeeded()) {
                                    mongoClient.removeOne("callbacks", new JsonObject().put("_id", new JsonObject().put("$oid", callbackId)), res -> {
                                        routingContext.response().end();
                                    });
                                } else {
                                    routingContext.fail(500);
                                }
                            });

                            break;
                        default:
                            routingContext.response().end();
                    }

                } else {
                    routingContext.fail(400);
                }
            });
        });

        instance.mountPublicSubRouter("/confirmation", router);

        vertx.createHttpServer().requestHandler(instance::accept).listen(port);
    }
}
