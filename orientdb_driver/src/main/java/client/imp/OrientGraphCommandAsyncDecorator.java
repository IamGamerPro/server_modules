package client.imp;


import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import io.vertx.core.*;

import java.util.function.Function;

/**
 * Created by Sergey Kobets on 20.02.2016.
 */
public final class OrientGraphCommandAsyncDecorator<T> {
    private final Vertx vertx;
    private final OrientGraph orientGraph;
    private final Context context;
    private final Function<OrientGraph, T> function;

    public OrientGraphCommandAsyncDecorator(Vertx vertx, OrientGraph orientGraph, Context context, Function<OrientGraph, T> function) {
        this.vertx = vertx;
        this.orientGraph = orientGraph;
        this.context = context;
        this.function = function;
    }

    private void handle(Future<T> future) {
        try {
            T result = function.apply(orientGraph);
            future.complete(result);
        } catch (Exception e) {
            future.fail(e);
        } finally {
            orientGraph.shutdown();
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
}
