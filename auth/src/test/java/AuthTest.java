import auth.OrientDBAuthProvider;
import client.OrientClient;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sergey.kobets on 24.02.2016.
 */
@RunWith(VertxUnitRunner.class)
public class AuthTest {

    private OrientClient orientClient;
    private OrientDBAuthProvider authProvider;
    private static Vertx vertx;

    public static void createVertexType(OrientGraphNoTx noTx, String name, Map<String, OType> property) {
        OrientVertexType existType = noTx.getVertexType(name);
        if (existType == null) {
            OrientVertexType vertexType = noTx.createVertexType(name);
            for (Map.Entry<String, OType> stringObjectEntry : property.entrySet()) {
                vertexType.createProperty(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            }
        }
    }

    @BeforeClass
    public static void beforeClass() {
        OrientGraphFactory orientGraphFactory = new OrientGraphFactory("plocal:/test");
        OrientGraphNoTx noTx = orientGraphFactory.getNoTx();
        try {
            if(noTx.getVertexType("User") != null) {
                noTx.dropVertexType("User");
            }
            Map<String, OType> fields = new HashMap<>();
            fields.put("login", OType.STRING);
            fields.put("password", OType.STRING);
            fields.put("salt", OType.STRING);
            createVertexType(noTx, "User", fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        orientGraphFactory.close();
        vertx = Vertx.vertx();
    }

    @Before
    public void before() {
        orientClient = OrientClient.createNonShared(vertx, new JsonObject().put("url", "plocal:/test"));
        authProvider = OrientDBAuthProvider.create(orientClient);
    }

    @Test
    public void test(TestContext context) {
        Async async = context.async(1);
        authProvider.authenticate(new JsonObject("{\"username\":\"test\", \"password\": \"dfltpwd\"}"), event -> {
            if (event.succeeded()) {
                System.out.println(event.result());
                async.countDown();
            } else {
                System.out.println(event.cause().getMessage());
                async.countDown();
            }
        });
        async.await();
    }
}
