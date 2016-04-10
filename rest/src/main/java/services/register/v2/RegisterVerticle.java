package services.register.v2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import org.bson.types.ObjectId;
import pro.iamgamer.core.security.PasswordUtils;
import pro.iamgamer.routing.RouteOrchestrator;

/**
 * Created by Sergey Kobets on 10.04.2016.
 * Experimental MongoDB Register
 */
public class RegisterVerticle extends AbstractVerticle {
    private MongoClient shared;

    @Override
    public void start() throws Exception {
        JsonObject config = context.config();
        JsonObject databaseConfig = config.getJsonObject("databaseConfig");
        Integer port = config.getJsonObject("httServerConfig").getInteger("port");
        shared = MongoClient.createShared(vertx, databaseConfig);
        RouteOrchestrator instance = RouteOrchestrator.getInstance(vertx, "/api");

        Router router = Router.router(vertx);
        router.post().handler(routingContext -> {
            JsonObject registerRequest = routingContext.getBodyAsJson();
            final String login = registerRequest.getString("login");
            final String password = registerRequest.getString("password");
            final String email = registerRequest.getString("email");
            vertx.<byte[][]>executeBlocking(future -> {
                try {
                    System.out.println(Thread.currentThread().getName());
                    final byte[] salt = PasswordUtils.randomSalt();
                    final byte[] hash = PasswordUtils.hash(password.toCharArray(), salt);
                    final byte[][] bytes = {hash, salt};
                    future.complete(bytes);
                } catch (Exception e) {
                    future.fail(e);
                }
            }, false, calc -> {
                System.out.println(Thread.currentThread().getName());
                if (calc.succeeded()) {
                    final byte[] hash = calc.result()[0];
                    final byte[] salt = calc.result()[1];
                    shared.createCollection("users",
                            v -> {
                                JsonObject document = new JsonObject();
                                document.put("login", login);
                                document.put("email", email);
                                document.put("password", new JsonObject().put("$binary", hash));
                                document.put("salt", new JsonObject().put("$binary", salt));
                                shared.insert("users", document, generatedId -> {
                                            if (generatedId.succeeded()) {
                                                routingContext.response().setStatusCode(201);
                                                routingContext.reroute(HttpMethod.POST, "/login");
                                            } else {
                                                routingContext.fail(generatedId.cause());
                                            }
                                        }
                                );
                            }
                    );
                }
            });
        });
        instance.mountPublicSubRouter("/register/v1/", router);
        vertx.createHttpServer().requestHandler(instance::accept).listen(port);

    }
}


