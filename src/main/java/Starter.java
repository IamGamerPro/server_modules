import io.vertx.core.DeploymentOptions;
import io.vertx.core.DeploymentOptionsConverter;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import pro.iamgamer.config.Configuration;
import services.login.LoginVerticle;
import services.register.RegisterVerticle;
import services.user.PrivateUserPageTest;

import java.time.LocalTime;

/**
 * Created by Sergey Kobets on 08.11.2015.
 * Запуск сервера в режиме разработки
 */

public class Starter {
    private static final Vertx vertx = Vertx.vertx();

    public static void main(String[] args) {
        DeploymentOptions deploymentOptions = readConfiguration();
        vertx.deployVerticle(new RegisterVerticle(), deploymentOptions);
        vertx.deployVerticle(new LoginVerticle(), deploymentOptions);
        vertx.deployVerticle(new PrivateUserPageTest(), deploymentOptions);
        System.out.println(LocalTime.now() + " server started successfully");
    }

    static DeploymentOptions readConfiguration(){
        Buffer buffer = vertx.fileSystem().readFileBlocking("config.json");
        JsonObject jsonObject = buffer.toJsonObject();
        return new DeploymentOptions().setConfig(jsonObject);
    }
}
