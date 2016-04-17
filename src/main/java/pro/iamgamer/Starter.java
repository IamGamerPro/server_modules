package pro.iamgamer;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import services.login.LoginVerticle;
import services.mail.ConfirmationCallbackVerticle;
import services.register.RegisterVerticle;
import services.user.UserPageVerticle;

import java.time.LocalTime;

/**
 * Created by Sergey Kobets on 08.11.2015.
 * Запуск сервера в режиме разработки
 */

public class Starter {
    private static final Vertx vertx = Vertx.vertx();

    public static void main(String[] args) {
        DeploymentOptions deploymentOptions = readConfiguration();
        vertx.deployVerticle(RegisterVerticle.class.getCanonicalName(), deploymentOptions);
        vertx.deployVerticle(LoginVerticle.class.getCanonicalName(), new DeploymentOptions(deploymentOptions).setInstances(10));
        vertx.deployVerticle(UserPageVerticle.class.getCanonicalName(), deploymentOptions);
        vertx.deployVerticle(ConfirmationCallbackVerticle.class.getCanonicalName(), deploymentOptions);
        System.out.println(LocalTime.now() + " server started successfully");
    }

    static DeploymentOptions readConfiguration() {
        Buffer buffer = vertx.fileSystem().readFileBlocking("config.json");
        JsonObject jsonObject = buffer.toJsonObject();
        return new DeploymentOptions().setConfig(jsonObject);
    }
}
