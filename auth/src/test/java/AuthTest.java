import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import pro.iamgamer.core.security.PasswordUtils;

import java.util.Arrays;
import java.util.List;


/**
 * Created by sergey.kobets on 24.02.2016.
 */
@RunWith(VertxUnitRunner.class)
public class AuthTest {
    @Test
    public void test(TestContext context) {
        Async async = context.async(1);
        Vertx vertx = Vertx.vertx();
        MongoClient nonShared = MongoClient.createNonShared(vertx, new JsonObject("{\n" +
                "    \"connection_string\": \"mongodb://localhost:27017\",\n" +
                "    \"db_name\": \"iamgamer\",\n" +
                "    \"useObjectId\": true\n" +
                "  }"));
        final String trololUser = "TrolololOL8";
        final String testString = "avt564180";
        final byte[] salt = PasswordUtils.randomSalt();

        final byte[] hash = PasswordUtils.hash(testString.toCharArray(), salt);
        JsonObject document = new JsonObject();
        document.put("login", trololUser);
        document.put("password", new JsonObject().put("$binary", hash));
        document.put("salt", new JsonObject().put("$binary", salt));
        nonShared.insert("users", document, result -> {
            if(result.failed()){
                async.countDown();
                return;
            }
            nonShared.find("users", new JsonObject().put("login", trololUser), result2->
            {
                List<JsonObject> result1 = result2.result();
                JsonObject jsonObject = result1.get(0);
                byte[] salt2 = jsonObject.getJsonObject("salt").getBinary("$binary");
                byte[] binary = jsonObject.getJsonObject("password").getBinary("$binary");
                boolean equals1 = Arrays.equals(hash, binary);
                boolean equals = Arrays.equals(salt, salt2);
                context.assertEquals(true, equals);
                context.assertEquals(true, equals1);

                byte[] hash1 = PasswordUtils.hash(testString.toCharArray(), salt2);
                boolean equals2 = Arrays.equals(hash, hash1);
                context.assertEquals(true, equals2);
               /* List<JsonObject> result1 = result2.result();
                JsonObject jsonObject = result1.get(0);
                byte[] binary = jsonObject.getJsonObject("password").getBinary("$binary");
                byte[] salt2 = jsonObject.getJsonObject("salt").getBinary("$binary");
                byte[] hash1 = PasswordUtils.hash(testString.toCharArray(), salt2);
                boolean equals = Arrays.equals(hash1, binary);
                context.assertEquals(true, equals);*/
                async.countDown();
            });
        });

        /*nonShared.find("users", new JsonObject().put("login", trololUser), result2->
        {
            List<JsonObject> result1 = result2.result();
            JsonObject jsonObject = result1.get(0);
            byte[] binary = jsonObject.getJsonObject("password").getBinary("$binary");
            byte[] salt = jsonObject.getJsonObject("salt").getBinary("$binary");
            byte[] hash1 = PasswordUtils.hash(testString.toCharArray(), salt);
            boolean equals = Arrays.equals(hash1, binary);
            context.assertEquals(true, equals);
            async.countDown();
        });
        async.await();*/
    }
}
