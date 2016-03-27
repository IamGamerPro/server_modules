package client;

import client.imp.OrientClientImp;
import com.google.common.annotations.Beta;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
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
    static OrientClient createShared(Vertx vertx, JsonObject config, String poolName){
        return new OrientClientImp(vertx, config, poolName);
    }

    @Beta
    OrientClient getGraph(Handler<AsyncResult<OrientGraphAsync>> handler);

    /**
     * Транзакционнонный доступ к графу без асинхронной обертки
     * Блокируюший метод!
     */
    OrientGraph getGraph();

    /**
     * Не транзакционный доступ к графу без асинхронной обертки
     * Блокируюший метод!
     */
    OrientGraphNoTx getGraphNoTx();

    /**
     * Транзакционный доступ к докуменно
     * Блокируюший метод!
     */
    ODatabaseDocumentTx getDocument();

    void close();
}
