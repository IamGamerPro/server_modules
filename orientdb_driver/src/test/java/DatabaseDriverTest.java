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

    @Repeat(760)
    @Test
    public void test(TestContext context) {
        Async async = context.async(1);
        long l = System.nanoTime();
        orientClient.getGraph(handler -> {
            if (handler.succeeded()) {
                OrientGraphAsync result = handler.result();
                result.command(buildRequest(new OCommandSQL("CREATE VERTEX EMPLOYEE CONTENT { \"name\" : \"Jay\", \"surname\" : \"Miner\", \"lol\" : ? }"), "as"), v -> {
                    System.out.println(TimeUnit.MILLISECONDS.convert(System.nanoTime() - l, TimeUnit.NANOSECONDS));
                    async.complete();
                });


            } else {
                async.complete();
            }
        });
    }

    @Repeat(10)
    @Test
    public void test2(TestContext context) {
        Async async = context.async(1);
        orientClient.getGraph(handler -> {
            if (handler.succeeded()) {
                OrientGraphAsync result = handler.result();
                result.command(orientGraph -> {
                    try {
                        orientGraph.begin();
                        OrientVertex orientVertex = orientGraph.addVertex("class:EMPLOYEE", "name", "Sergey", "sex", "male");
                        orientGraph.commit();
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
