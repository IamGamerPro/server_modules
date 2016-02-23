package client;

import client.imp.ParamsRequest;
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
    OrientGraphAsync command(ParamsRequest request, Handler<AsyncResult<Void>> resultHandler);

    /**
     * Выполнить произвольный код в транзакции вернуть значение
     * @param function бизнес логика выполняемая в транзакционном графе
     * @param handler функция обратного вызова завершения обработки бизнес логики
     * @param <T> тип возращаемого значения
     */
    <T> OrientGraphAsync command(Function<OrientGraph, T> function, Handler<AsyncResult<T>> handler);

}
