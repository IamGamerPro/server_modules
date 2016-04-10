import client.OrientClient;
import client.OrientGraphAsync;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Repeat;
import io.vertx.ext.unit.junit.RepeatRule;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static client.imp.ParamsRequest.buildRequest;

/**
 * Created by Sergey Kobets on 20.02.2016.
 */
@RunWith(VertxUnitRunner.class)
public class DatabaseDriverTest {
    private OrientClient orientClient;
    private static Vertx vertx;

    @Rule
    public RepeatRule rule = new RepeatRule();

    @BeforeClass
    public static void beforeClass() {
        OrientGraphFactory orientGraphFactory = new OrientGraphFactory("plocal:/test");
        OrientGraphNoTx noTx = orientGraphFactory.getNoTx();
        try {
            noTx.command(new OCommandSQL("DROP CLASS EMPLOYEE UNSAFE")).execute();
            noTx.command(new OCommandSQL("CREATE CLASS EMPLOYEE EXTENDS V")).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        orientGraphFactory.close();
        vertx = Vertx.vertx();
    }

    @Before
    public void init() {
        orientClient = OrientClient.createShared(vertx, new JsonObject().put("url", "plocal:/test"), "as");

    }

    @Repeat(100)
    @Test
    public void test(TestContext context) {
        Async async = context.async(1);
        long l = System.nanoTime();
        orientClient.getGraph(handler -> {
            if (handler.succeeded()) {
                OrientGraphAsync result = handler.result();
                OCommandSQL request = new OCommandSQL("CREATE VERTEX EMPLOYEE CONTENT { \"name\" : \"Jay\", \"surname\" : \"Miner\", \"lol\" : ? }");
                result.command(buildRequest(request, "as"), v -> {
                    System.out.println(TimeUnit.MILLISECONDS.convert(System.nanoTime() - l, TimeUnit.NANOSECONDS));
                    async.complete();
                });


            } else {
                async.complete();
            }
        });
    }

    @Repeat(100)
    @Test
    public void test2(TestContext context) {
        Async async = context.async(1);
        orientClient.getGraph(handler -> {
            if (handler.succeeded()) {
                OrientGraphAsync result = handler.result();
                result.command(orientGraph -> {
                    try {
                        long l = System.nanoTime();
                        orientGraph.begin();
                        OrientVertex orientVertex = orientGraph.addVertex("class:EMPLOYEE", "name", "Sergey", "sex", "male");
                        orientGraph.commit();
                        long l1 = System.nanoTime();
                        long l2 = TimeUnit.NANOSECONDS.toMillis(l1 - l);
                        System.out.println(l2);
                        return orientVertex.getId();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, event -> {
                    if (event.succeeded()) {
                        Object generatedId = event.result();
                        System.out.println(generatedId);
                        async.complete();
                    } else {
                        async.complete();
                    }
                });
            } else {
                async.complete();
            }
        });
        async.await();
    }
}
