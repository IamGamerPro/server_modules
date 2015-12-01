package pro.iamgamer.core.database.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import pro.iamgamer.core.model.User;
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
        final OSchemaProxy schema = persistentContext.getConnection().getMetadata().getSchema();
        if(!schema.existsClass(User.class.getSimpleName())){
            schema.createClass(User.class);
        }

    }
}
