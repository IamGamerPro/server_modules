import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.orientechnologies.orient.core.record.impl.ODocument;
import pro.iamgamer.core.DatabaseManagement;
import pro.iamgamer.core.database.repository.EmbeddedDBModule;
import pro.iamgamer.core.database.repository.UserRepository;

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
        ODocument entries = instance.create();
        System.out.println("lol");
    }
}
