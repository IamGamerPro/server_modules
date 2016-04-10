import pro.iamgamer.auth.orient.OrientDBAuthProvider;
import pro.iamgamer.auth.orient.imp.security.PasswordUtils;
import client.OrientClient;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Repeat;
import io.vertx.ext.unit.junit.RepeatRule;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Created by sergey.kobets on 24.02.2016.
 */
@RunWith(VertxUnitRunner.class)
public class AuthTest {

    private static OrientClient orientClient;
    private static OrientDBAuthProvider authProvider;
    private static Vertx vertx;
    private static List<Long> time = new ArrayList<>();
    private static final JsonObject user = new JsonObject("{\"login\":\"testSuccess\", \"password\": \"dfltpwd\"}");

    @Rule
    public RepeatRule rule = new RepeatRule();

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
            if (noTx.getVertexType("User") != null) {
                noTx.dropVertexType("User");
            }
            Map<String, OType> fields = new HashMap<>();
            fields.put("login", OType.STRING);
            fields.put("password", OType.BINARY);
            fields.put("salt", OType.BINARY);
            createVertexType(noTx, "User", fields);
            byte[] salt = PasswordUtils.randomSalt();
            byte[] hash = PasswordUtils.hash("dfltpwd".toCharArray(), salt);
            noTx.addVertex("class:User", "login", "testSuccess", "salt", salt, "password", hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        orientGraphFactory.close();
        vertx = Vertx.vertx();
        orientClient = OrientClient.createShared(vertx, new JsonObject().put("url", "plocal:/test"), "loginPool");
        authProvider = OrientDBAuthProvider.create(orientClient);
    }

    @Test
    public void incorrectLogin(TestContext context) {
        Async async = context.async(1);
        authProvider.authenticate(new JsonObject("{\"login\":\"test\", \"password\": \"dfltpwd\"}"), event -> {
            context.assertTrue(event.failed());
            context.assertEquals(event.cause().getMessage(), "Не правильное имя пользователя или пароль");
            async.countDown();
        });
        async.await();
    }

    @Test
    @Repeat(100)
    public void correctLogin(TestContext context) {
        Async async = context.async(1);
        long start = System.nanoTime();
        authProvider.authenticate(user, event ->
        {
            context.assertTrue(event.succeeded());
            time.add(System.nanoTime() - start);
            async.countDown();
        });
        async.await();
    }

    @AfterClass
    public static void afterClass() {
        Optional<Long> min = time.stream().min(Long::compare);
        Optional<Long> max = time.stream().max(Long::compare);
        long bestTime = TimeUnit.MILLISECONDS.convert(min.get(), TimeUnit.NANOSECONDS);
        long worstTime = TimeUnit.MILLISECONDS.convert(max.get(), TimeUnit.NANOSECONDS);
        System.out.println("Login bestTime: " + bestTime + "ms worstTime:" + worstTime + "ms");
    }
}
