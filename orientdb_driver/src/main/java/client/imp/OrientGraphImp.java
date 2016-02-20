package client.imp;

import client.OrientGraphAsync;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

/**
 * Created by Sergey Kobets on 20.02.2016.
 */
public class OrientGraphImp implements OrientGraphAsync {
    private final OrientGraph orientGraph;
    private final Vertx vertx;
    private final Context context;

    public OrientGraphImp(Vertx vertx, OrientGraph orientGraph) {
        this.vertx = vertx;
        this.orientGraph = orientGraph;
        this.context = ((VertxInternal) vertx).createWorkerContext(false, null, new JsonObject(), getClassLoader());
    }

    private ClassLoader getClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader == null ? getClass().getClassLoader() : contextClassLoader;
    }

    @Override
    public OrientGraphAsync command(OCommandRequest request, Handler<AsyncResult<Void>> resultHandler) {
        new OrientGraphCommandAsyncDecorator<Void>(vertx, orientGraph, context, (orientGraph) -> {
            orientGraph.begin();
            orientGraph.command(request).execute();
            orientGraph.commit();
            return null;
        }).execute(resultHandler);
        return this;
    }

    @Override
    public <T> OrientGraphAsync command(Function<OrientGraph, T> transactionalFunction, Handler<AsyncResult<T>> resultHandler) {
        new OrientGraphCommandAsyncDecorator<>(vertx, orientGraph, context, transactionalFunction::apply).execute(resultHandler);
        return this;
    }
}
