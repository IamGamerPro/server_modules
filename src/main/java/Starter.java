import com.google.inject.Guice;
import com.google.inject.Injector;
import com.orientechnologies.orient.core.id.ORID;
import pro.iamgamer.core.DatabaseManagement;
import pro.iamgamer.core.database.repository.EmbeddedDBModule;
import pro.iamgamer.core.database.repository.UserRepository;
import pro.iamgamer.core.database.dao.UserTest;
import pro.iamgamer.core.model.User;


/**
 * Created by Sergey Kobets on 08.11.2015.
 * Запуск сервера в режиме разработки
 */

public class Starter {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new EmbeddedDBModule());
        DatabaseManagement instance1 = injector.getInstance(DatabaseManagement.class);
        instance1.start();
        UserRepository instance = injector.getInstance(UserRepository.class);
        final UserTest instance2 = injector.getInstance(UserTest.class);
        final User user = new User("test1");
        final ORID insert = instance.register(user, "qwerty1234");
        final User login = instance2.login("test1", "qwerty1234");
        System.out.println(login);
    }
}
