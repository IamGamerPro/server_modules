package services.register.v2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
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
        JsonObject config = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name", "iamgamer")
                .put("useObjectId", true);
        shared = MongoClient.createShared(vertx, config);

        RouteOrchestrator instance = RouteOrchestrator.getInstance(vertx, "/api");

        Router router = Router.router(vertx);

        router.post().handler(BodyHandler.create());
        router.post().handler(routingContext -> {
            JsonObject bodyAsJson = routingContext.getBodyAsJson();
            UserCreateRequest obj = new UserCreateRequest(bodyAsJson);
            routingContext.put("registerRequest", obj);
            routingContext.next();
        });
        router.post().blockingHandler(routingContext -> {
            UserCreateRequest registerRequest = routingContext.get("registerRequest");
            final byte[] salt = PasswordUtils.randomSalt();
            final byte[] hash = PasswordUtils.hash(registerRequest.password.toCharArray(), salt);
            shared.createCollection("users",
                    v -> {
                        JsonObject document = new JsonObject();
                        document.put("login", registerRequest.login);
                        document.put("email", registerRequest.email);
                        document.put("password", new JsonObject().put("$binary", hash));
                        document.put("salt", new JsonObject().put("$binary", salt));
                        shared.insert("users", document, generatedId -> {
                                    if (generatedId.succeeded()) {
                                        ObjectId objectId = new ObjectId(generatedId.result());
                                        System.out.println(objectId.getDate());
                                    }
                                }
                        );
                    }
            );
            routingContext.next();
        }, false);
        router.post().handler(handler -> handler.response().setStatusCode(200).end());

        instance.mountPublicSubRouter("/register/v2/", router);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private class UserCreateRequest {
        private final String login;
        private final String password;
        private final String email;

        public UserCreateRequest(JsonObject registerRequest) {
            this.login = registerRequest.getString("login");
            this.password = registerRequest.getString("password");
            this.email = registerRequest.getString("email");
        }
    }

    @Override
    public void stop() throws Exception {
        shared.close();
    }
}
