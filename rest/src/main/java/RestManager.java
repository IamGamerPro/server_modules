import io.vertx.core.Vertx;
import services.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by sergey.kobets on 08.12.2015.
 */
@Singleton
public class RestManager {
    private Vertx vertx;
    @Inject
    TestL test;
    @Inject
    UserService userService;

    public void start(){
        vertx = Vertx.vertx();
        vertx.deployVerticle(test);
        vertx.deployVerticle(userService);
    }
    public void stop(){
        vertx.close();
    }
}
