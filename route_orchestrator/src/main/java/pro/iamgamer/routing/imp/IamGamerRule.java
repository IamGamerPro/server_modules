package pro.iamgamer.routing.imp;

import auth.OrientDBAuthProvider;
import client.OrientClient;
import com.google.common.collect.Sets;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import pro.iamgamer.routing.RouteOrchestratorRule;
import pro.iamgamer.routing.csrf.PersistCSRFHandler;

import java.util.Set;

/**
 * Created by Sergey Kobets on 09.03.2016.
 */
public class IamGamerRule implements RouteOrchestratorRule {
    private static final Set<Handler<RoutingContext>> handlers =
            Sets.newHashSet(BodyHandler.create(), CorsHandler.create("*"));

    @Override
    public Set<Handler<RoutingContext>> baseHandlers() {
        return handlers;
    }
}
