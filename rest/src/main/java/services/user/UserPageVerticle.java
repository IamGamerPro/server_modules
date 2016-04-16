package services.user;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;
import pro.iamgamer.routing.RouteOrchestrator;


/**
 * Created by sergey.kobets on 14.12.2015.
 */
public class UserPageVerticle extends AbstractVerticle {
    private MongoClient mongoClient;
    private JsonObject databaseConfig;
    private Integer port;
    private RouteOrchestrator routeOrchestrator;

    @Override
    public void start() throws Exception {
        serviceInitialization();
        Router router = Router.router(vertx);
        router.get().handler(this::getUser);
        routeOrchestrator.mountRequiresAuthorizationSubRouter("/user", router);

        vertx.createHttpServer().requestHandler(routeOrchestrator::accept).listen(port);
    }

    private void serviceInitialization() {
        JsonObject config = context.config();
        databaseConfig = config.getJsonObject("databaseConfig");
        port = config.getJsonObject("httServerConfig").getInteger("port");
        mongoClient = MongoClient.createShared(vertx, databaseConfig);
        routeOrchestrator = RouteOrchestrator.getInstance(vertx, "/api");
    }

    private void getUser(RoutingContext routingContext) {
        User user = routingContext.user();
        String currentUser;
        if(user != null){
            String login = user.principal().getString("login");
            if(login != null){
                currentUser = login;
            }
        }
        JsonObject excludeSelector = new JsonObject();
        excludeSelector.put("password", 0).put("salt", 0);

        JsonObject selector = new JsonObject();
        HttpServerRequest request = routingContext.request();
        if(request.getParam("id") != null){
            String id = request.getParam("id");
            selector.put("_id", id);
        }else if(request.getParam("name") != null){
            String name = request.getParam("name");
            selector.put("login", name);
        }else if(request.getParam("email") != null){
            String email = request.getParam("email");
            selector.put("emails", email);
        }
        mongoClient.findOne("users", selector, excludeSelector, (AsyncResultHandler<JsonObject>) event -> {
            JsonObject result = event.result();
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(result.encode());
        });
    }

    @Override
    public void stop() throws Exception {
        mongoClient.close();
    }
}
