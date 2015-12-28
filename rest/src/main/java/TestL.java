import services.CoreVerticleImp;


/**
 * Created by sergey.kobets on 08.12.2015.
 */
public class TestL extends CoreVerticleImp {
    @Override
    protected void concrete() {
        router.get("/api/private/test/1").handler(event -> {
            event.response()
                    .putHeader("content-type", "text/plain; charset=UTF-8").end("test");
        });
    }
}
