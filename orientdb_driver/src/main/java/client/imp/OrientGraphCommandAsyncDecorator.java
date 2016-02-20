package client.imp;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import io.vertx.core.*;

/**
 * Created by Sergey Kobets on 20.02.2016.
 */
public abstract class OrientGraphCommandAsyncDecorator<T> {
    protected final Vertx vertx;
    protected final OrientGraph orientGraph;
    protected final Context context;

    public OrientGraphCommandAsyncDecorator(Vertx vertx, OrientGraph orientGraph, Context context) {
        this.vertx = vertx;
        this.orientGraph = orientGraph;
        this.context = context;
    }

    public void handle(Future<T> future) {
        try {
            T result = execute(orientGraph);
            future.complete(result);
        } catch (Exception e) {
            future.fail(e);
        }
    }

    public void execute(Handler<AsyncResult<T>> resultHandler) {
        Future<T> f = Future.future();
        Context callbackContext = vertx.getOrCreateContext();
        context.runOnContext(v -> {
            f.setHandler(ar -> callbackContext.runOnContext(v2 -> resultHandler.handle(ar)));
            handle(f);
        });

    }

    protected abstract T execute(OrientGraph graph);
}
