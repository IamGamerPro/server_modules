package services.user;

import com.google.common.base.MoreObjects;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import pro.iamgamer.routing.RouteOrchestrator;
import services.user.dao.UserPageDAO;
import services.user.model.BaseUserPageData;

import java.nio.charset.Charset;
import java.util.Optional;


/**
 * Created by sergey.kobets on 14.12.2015.
 */
public class UserPageVerticle extends AbstractVerticle {
    private MongoClient mongoClient;
    private JsonObject databaseConfig;
    private Integer port;
    private RouteOrchestrator routeOrchestrator;
    private final JsonObject baseExcludeSelector = new JsonObject("{\"password\":0, \"salt\":0}");
    private UserPageDAO dao;

    @Override
    public void start() throws Exception {
        serviceInitialization();
        Router router = Router.router(vertx);
        router.get().handler(this::getUserPage);
        router.put().handler(this::updateUserPage);
        router.delete("/avatar").handler(this::deleteAvatar);
        routeOrchestrator.mountRequiresAuthorizationSubRouter("/user", router);
        vertx.createHttpServer().requestHandler(routeOrchestrator::accept).listen(port);
    }

    private void deleteAvatar(RoutingContext routingContext) {
        Optional<String> userId = Optional.ofNullable(routingContext.user())
                .map(User::principal)
                .map(c -> c.getString("userId"));
        if (userId.isPresent()) {
            dao.deleteAvatar(userId.get(), routingContext);
        } else {
            routingContext.fail(403);
        }
    }

    private void updateUserPage(RoutingContext routingContext) {
        Optional<String> userId = Optional.ofNullable(routingContext.user())
                .map(User::principal)
                .map(c -> c.getString("userId"));
        if (userId.isPresent()) {
            BaseUserPageData baseUserPageData = Json.decodeValue(routingContext.getBody().toString(Charset.forName("UTF-8")), BaseUserPageData.class);
            dao.update(baseUserPageData, userId.get(), routingContext);
        } else {
            routingContext.fail(403);
        }

    }

    private void serviceInitialization() {
        JsonObject config = context.config();
        databaseConfig = config.getJsonObject("databaseConfig");
        port = config.getJsonObject("httServerConfig").getInteger("port");
        mongoClient = MongoClient.createShared(vertx, databaseConfig);
        dao = UserPageDAO.getDAO(mongoClient);
        routeOrchestrator = RouteOrchestrator.getInstance(vertx, "/api");
    }

    private void getUserPage(RoutingContext routingContext) {
        User user = routingContext.user();
        final String currentUser = getCurrentLogin(user);
        HttpServerRequest request = routingContext.request();
        JsonObject selector;
        try {
            selector = getQuerySelector(request);
        } catch (Exception e) {
            routingContext.fail(400);
            return;
        }
        mongoClient.findOne("users", selector, baseExcludeSelector, (AsyncResultHandler<JsonObject>) event -> {
            JsonObject result = event.result();
            if (!(currentUser != null && currentUser.equals(result.getString("login")))) {
                result.remove("emails");
                result.put("relation", 0);
            } else {
                result.put("relation", -1);
            }
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(result.encode());
        });

    }

    private JsonObject getQuerySelector(HttpServerRequest request) {
        JsonObject selector = new JsonObject();
        if (request.getParam("id") != null) {
            String id = request.getParam("id");
            selector.put("_id", id);
        } else if (request.getParam("name") != null) {
            String name = request.getParam("name");
            selector.put("login", name);
        } else {
            throw new RuntimeException();
        }

        return selector;
    }

    private String getCurrentLogin(User user) {
        String currentUser;
        if (user != null) {
            String login = MoreObjects.firstNonNull(
                    user.principal().getString("sub"),
                    user.principal().getString("login"));
            if (login != null) {
                currentUser = login;
            } else {
                currentUser = null;
            }
        } else {
            currentUser = null;
        }
        return currentUser;
    }

    @Override
    public void stop() throws Exception {
        mongoClient.close();
    }
}
