import com.google.inject.Guice;
import com.google.inject.Injector;
import pro.iamgamer.core.DatabaseManagement;
import pro.iamgamer.core.database.dao.AuthenticationDao;
import pro.iamgamer.core.database.core.EmbeddedDBModule;
import pro.iamgamer.core.model.User;


/**
 * Created by Sergey Kobets on 08.11.2015.
 * Запуск сервера в режиме разработки
 */

public class Starter {

    public static void main(String[] args) {
        /*шикарно ЖВ*/
        Injector injector = Guice.createInjector(new EmbeddedDBModule(), new BasicWebModule());
        DatabaseManagement instance1 = injector.getInstance(DatabaseManagement.class);
        instance1.start();
        AuthenticationDao instance = injector.getInstance(AuthenticationDao.class);
        /*final User user = new User("test6");
        final ORID insert = instance.register(user, "qwerty1234");*/
        final User login = instance.login("test1", "qwerty1234");
        final RestManager instance2 = injector.getInstance(RestManager.class);
        instance2.start();
    }
}
