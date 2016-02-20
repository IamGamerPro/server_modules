package client;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.function.Function;

/**
 * Created by Sergey Kobets on 20.02.2016.
 */
public interface OrientGraphAsync {
    /**
     * Выполнить запрос без параметров не возвращая результат
     */
    OrientGraphAsync command(OCommandRequest request, Handler<AsyncResult<Void>> resultHandler);

    <T> OrientGraphAsync command(Function<OrientGraph, T> function, Handler<AsyncResult<T>> handler);

}
