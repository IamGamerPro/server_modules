import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import services.user.PrivateUserPageTest;
import services.register.RegisterVerticle;

import java.time.LocalTime;

/**
 * Created by Sergey Kobets on 08.11.2015.
 * Запуск сервера в режиме разработки
 */

public class Starter {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        vertx.deployVerticle(new RegisterVerticle());
        vertx.deployVerticle(new PrivateUserPageTest());
        System.out.println(LocalTime.now() + " server started successfully");
    }
}
