package client;

import client.imp.OrientClientImp;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.UUID;


/**
 * Created by Sergey Kobets on 20.02.2016.
 */
public interface OrientClient {
    /**
     * @param vertx
     * @param config {"url":"", "login" :"", "pwd":""}
     * @return
     */
    static OrientClient createNonShared(Vertx vertx, JsonObject config) {
        return new OrientClientImp(vertx, config, UUID.randomUUID().toString());
    }

    OrientClient getGraph(Handler<AsyncResult<OrientGraphAsync>> handler);

    void close();
}
