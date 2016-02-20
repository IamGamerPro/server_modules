package client;

import com.orientechnologies.orient.core.command.OCommandRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by Sergey Kobets on 20.02.2016.
 */
public interface OrientGraphAsync {
    /**Выполнить запрос не возвращая результат*/
    OrientGraphAsync command(OCommandRequest request, Handler<AsyncResult<Void>> resultHandler);
}
