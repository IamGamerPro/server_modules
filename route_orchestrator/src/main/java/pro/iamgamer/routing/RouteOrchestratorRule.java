package pro.iamgamer.routing;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Sergey Kobets on 04.03.2016.
 */
public interface RouteOrchestratorRule {
    default Set<Handler<RoutingContext>> baseHandlers() {
        return Collections.emptySet();
    }

    default String privateUrlPatch() {
        return "/private";
    }

    default String loginUrl() {
        return "/login";
    }
}
