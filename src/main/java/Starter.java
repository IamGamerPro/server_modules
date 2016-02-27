import io.vertx.core.Vertx;
import services.register.RegisterVerticle;

import java.time.LocalTime;

/**
 * Created by Sergey Kobets on 08.11.2015.
 * Запуск сервера в режиме разработки
 */

public class Starter {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RegisterVerticle());
        System.out.println(LocalTime.now() + " server started successfully");
    }
}
