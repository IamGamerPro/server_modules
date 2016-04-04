import io.vertx.core.Vertx;
import pro.iamgamer.config.Configuration;
import services.register.RegisterVerticle;
import services.user.PrivateUserPageTest;

import java.time.LocalTime;

/**
 * Created by Sergey Kobets on 08.11.2015.
 * Запуск сервера в режиме разработки
 */

public class Starter {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Configuration.publishBaseConfiguration(vertx, "config.json");
        vertx.deployVerticle(new RegisterVerticle());
        vertx.deployVerticle(new PrivateUserPageTest());
        System.out.println(LocalTime.now() + " server started successfully");
    }
}
