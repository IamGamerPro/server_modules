package services.configuration;

import com.google.common.collect.Sets;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import pro.iamgamer.routing.RouteOrchestratorRule;

import java.util.Set;

/**
 * Created by Sergey Kobets on 04.03.2016.
 */
public class RouteOrchestratorRuleImp implements RouteOrchestratorRule {
    private static final Set<Handler<RoutingContext>> handlers =
            Sets.newHashSet(BodyHandler.create(), CorsHandler.create("*"));

    @Override
    public Set<Handler<RoutingContext>> baseHandlers() {
        return handlers;
    }
}
