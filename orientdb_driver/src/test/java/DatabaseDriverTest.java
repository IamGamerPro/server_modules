import client.OrientClient;
import client.OrientGraphAsync;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
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
        orientClient = OrientClient.createNonShared(vertx, new JsonObject().put("url", "plocal:/test"));
    }

    @Test
    @Repeat(500)
    public void test(TestContext context) {
        Async async = context.async(1);
        long l = System.nanoTime();
        orientClient.getGraph(handler -> {
            if (handler.succeeded()) {
                OrientGraphAsync result = handler.result();
                result.command(new OCommandSQL("CREATE VERTEX EMPLOYEE CONTENT { \"name\" : \"Jay\", \"surname\" : \"Miner\", \"lol\" : ? }"), v -> {
                    System.out.println(System.nanoTime() - l);
                    async.complete();
                });


            } else {
                async.complete();
                System.out.println("lol");
            }
        });
    }
}
