package pro.iamgamer.core.database.core;

import com.google.inject.AbstractModule;
import ru.vyarus.guice.persist.orient.OrientModule;
import ru.vyarus.guice.persist.orient.RepositoryModule;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;

/**
 * Created by Sergey Kobets on 08.11.2015.
 */
public class EmbeddedDBModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new OrientModule("plocal:test", "admin", "admin")
                .autoCreateLocalDatabase(true));
        install(new RepositoryModule());
        bind(SchemeInitializer.class).to(SchemeInitializerImp.class);
    }
}
