package client.imp;

import client.OrientClient;
import client.OrientGraphAsync;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sergey Kobets on 20.02.2016.
 */
public class OrientClientImp implements OrientClient {
    private static final String GRAPH_FACTORY_LOCAL_MAP_NAME = "iamgamer.shared.OrientDB.OrientGraphFactory";
    private final Vertx vertx;
    private final OrientGraphFactoryHolder orientGraphFactoryHolder;
    private final ExecutorService exec;
    private final OrientGraphFactory orientGraphFactory;


    public OrientClientImp(Vertx vertx, JsonObject config, String poolName) {
        Objects.requireNonNull(vertx);
        Objects.requireNonNull(config);
        Objects.requireNonNull(poolName);
        this.vertx = vertx;
        this.orientGraphFactoryHolder = lookupHolder(poolName, config);
        this.exec = orientGraphFactoryHolder.exec();
        this.orientGraphFactory = orientGraphFactoryHolder.orientGraphFactory();

    }

    @Override
    public void close() {
        orientGraphFactoryHolder.close();
    }

    @Override
    public OrientClient getGraph(Handler<AsyncResult<OrientGraphAsync>> handler) {
        Context ctx = vertx.getOrCreateContext();
        exec.execute(() -> {
            Future<OrientGraphAsync> future = Future.future();
            try {
                OrientGraph tx = orientGraphFactory.getTx();
                OrientGraphAsyncImp orientGraphAsyncImp = new OrientGraphAsyncImp(vertx, tx);
                future.complete(orientGraphAsyncImp);
            } catch (Exception e) {
                future.fail(e);
            }
            ctx.runOnContext(v -> future.setHandler(handler));
        });
        return this;
    }

    private OrientGraphFactoryHolder lookupHolder(String poolName, JsonObject config) {
        synchronized (vertx) {
            LocalMap<String, OrientGraphFactoryHolder> cashedPool = vertx.sharedData().getLocalMap(GRAPH_FACTORY_LOCAL_MAP_NAME);
            OrientGraphFactoryHolder orientGraphFactoryHolder = cashedPool.get(poolName);
            if (orientGraphFactoryHolder == null) {
                orientGraphFactoryHolder = new OrientGraphFactoryHolder(config, () -> removeFromMap(cashedPool, poolName));
                cashedPool.put(poolName, orientGraphFactoryHolder);
            } else {
                orientGraphFactoryHolder.incRefCount();
            }
            return orientGraphFactoryHolder;
        }

    }

    private void removeFromMap(LocalMap<String, OrientGraphFactoryHolder> map, String dataSourceName) {
        synchronized (vertx) {
            map.remove(dataSourceName);
            if (map.isEmpty()) {
                map.close();
            }
        }
    }

    private static class OrientGraphFactoryHolder implements Shareable {
        private volatile OrientGraphFactory graphFactory;
        private volatile ExecutorService exec;
        private final Runnable closeRunner;
        private final JsonObject config;
        private final AtomicInteger clientCount = new AtomicInteger(1);

        OrientGraphFactoryHolder(JsonObject config, Runnable closeRunner) {
            this.config = config;
            this.closeRunner = closeRunner;
        }

        OrientGraphFactory orientGraphFactory() {
            if (graphFactory == null) {
                synchronized (this) {
                    if (graphFactory == null) {
                        Optional<String> url = Optional.ofNullable(config.getString("url"));
                        Optional<String> login = Optional.ofNullable(config.getString("login"));
                        Optional<String> pwd = Optional.ofNullable(config.getString("pwd"));
                        if (!url.isPresent()) {
                            throw new RuntimeException();
                        }
                        graphFactory = (login.isPresent() && pwd.isPresent())
                                ? new OrientGraphFactory(url.get(), login.get(), pwd.get())
                                : new OrientGraphFactory(url.get());
                        graphFactory.setupPool(50, 50);
                    }
                }
            }
            return graphFactory;

        }

        void incRefCount() {
            clientCount.incrementAndGet();
        }

        ExecutorService exec() {
            if (exec == null) {
                synchronized (this) {
                    if (exec == null) {
                        exec = new ThreadPoolExecutor(1, 1,
                                1000L, TimeUnit.MILLISECONDS,
                                new LinkedBlockingQueue<>(),
                                (r -> new Thread(r, "iamgamer-orient-service-get-graph")));
                    }
                }
            }
            return exec;
        }

        void close() {
            if (clientCount.decrementAndGet() == 0) {
                if (graphFactory != null) {
                    graphFactory.close();
                }
                if (closeRunner != null) {
                    closeRunner.run();
                }
            }
        }
    }

}
