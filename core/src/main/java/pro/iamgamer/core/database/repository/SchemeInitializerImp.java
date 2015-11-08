package pro.iamgamer.core.database.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import pro.iamgamer.core.model.Lol;
import ru.vyarus.guice.persist.orient.db.PersistentContext;
import ru.vyarus.guice.persist.orient.db.pool.DocumentPool;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;


import javax.inject.Inject;

/**
 * Created by Sergey Kobets on 08.11.2015.
 */
public class SchemeInitializerImp implements SchemeInitializer {
    @Inject
    PersistentContext<ODatabaseDocumentTx> persistentContext;

    @Override
    public void initialize() {
        //persistentContext.getConnection().getMetadata().getSchema().createClass(Lol.class);
        System.out.println("lol");
    }
}
