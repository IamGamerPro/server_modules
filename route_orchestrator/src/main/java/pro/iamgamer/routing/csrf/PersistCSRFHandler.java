package pro.iamgamer.routing.csrf;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Sergey Kobets on 12.03.2016.
 */
public interface PersistCSRFHandler extends Handler<RoutingContext> {
    String DEFAULT_HEADER_NAME = "X-XSRF-TOKEN";
    long DEFAULT_INVALIDATION_TIMEOUT = 60 * 60 * 1000;
    long DEFAULT_REGENERATION_TIMEOUT = 15 * 60 * 1000;

    static PersistCSRFHandler create(String secret) {
        return new PersistCSRFHandlerImp(secret);
    }

    String generateToken();

    PersistCSRFHandler setHeaderName(String name);

    PersistCSRFHandler setNagHttps(boolean nag);

    PersistCSRFHandler setTimeouts(long regeneration, long invalidation);
}
