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

/**
 * Created by Sergey Kobets on 20.02.2016.
 * <p>
 * TODO Вопросы о странном синхронайзед коде:
 * использование конструкции синхронайзед даже для инкрементирования счетчика
 * выглядит крайне странным, однако такой подход всегда используется в коробочных реализациях
 * вертекса. Это наводит на мысли относительно магии которая может стоять за Shareable
 * и о том как она влияет на использование тех же атомиков
 * До тщательного исследования этого вопроса повторю подход, но если это возможно
 * необходимо переделать на менее прожорливую синхронизацию.
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
            try{
                OrientGraph tx = orientGraphFactory.getTx();
                OrientGraphImp orientGraphImp = new OrientGraphImp(vertx, tx);
                future.complete(orientGraphImp);
            }catch (Exception e){
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

    /*протокол холдера предполагает Shareable
    * т.е. я создаю некоторый объект и публикую его по уникальному идентификатору
    * в распределенной мапе, идентификатор такого объекта просто строка
    * все замечательно но объект графа не тред сейф значит нельзя
    * просто в лоб его использовать из нескольких потоков
    * необходимо либо шарить что- то вроде пула от которого уже потом брать конекты
    * либо синхронизировать сам протокол доступа
    *
    */
    private static class OrientGraphFactoryHolder implements Shareable {
        OrientGraphFactory graphFactory;
        Runnable closeRunner;
        ExecutorService exec;
        JsonObject config;
        int refCount = 1;

        public OrientGraphFactoryHolder(JsonObject config, Runnable closeRunner) {
            this.config = config;
            this.closeRunner = closeRunner;
        }

        synchronized OrientGraphFactory orientGraphFactory() {
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
            return graphFactory;

        }

        synchronized void incRefCount() {
            refCount++;
        }

        synchronized ExecutorService exec() {
            if (exec == null) {
                exec = new ThreadPoolExecutor(1, 1,
                        1000L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(),
                        (r -> new Thread(r, "iamgamer-orient-service-get-graph-factory")));
            }
            return exec;
        }

        synchronized void close() {
            if (--refCount == 0) {
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
