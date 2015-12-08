package pro.iamgamer.core.database;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import pro.iamgamer.core.model.User;
import ru.vyarus.guice.persist.orient.db.PersistentContext;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;

import javax.inject.Inject;


/**
 * Created by Sergey Kobets on 08.11.2015.
 */
public class SchemeInitializerImp implements SchemeInitializer {
    @Inject
    PersistentContext<ODatabaseDocumentTx> persistentContext;
    private OSchemaProxy schema;

    @Override
    public void initialize() {
        schema = persistentContext.getConnection().getMetadata().getSchema();
        initUser();
    }

    private void initUser() {
        if (!schema.existsClass(User.class.getSimpleName())) {
            final OClass aClass = schema.createClass(User.class);
            aClass.setStrictMode(true);
        }
        final OClass userClass = schema.getClass(User.class);

        saveCreteProperty(userClass, "version", OType.STRING);
        saveCreteProperty(userClass, "modDate", OType.DATETIME);
        final OProperty login = saveCreteProperty(userClass, "login", OType.STRING);
        login.setMax("50");
        if(!userClass.areIndexed("login")){
            login.createIndex(OClass.INDEX_TYPE.UNIQUE);
        }
        saveCreteProperty(userClass, "password", OType.BINARY);
        saveCreteProperty(userClass, "salt", OType.BINARY);
    }

    private OProperty saveCreteProperty(OClass orientClass, String propertyName, OType type) {
        if (!orientClass.existsProperty(propertyName)) {
            return orientClass.createProperty(propertyName, type);
        } else {
            return orientClass.getProperty(propertyName);
        }
    }
}
