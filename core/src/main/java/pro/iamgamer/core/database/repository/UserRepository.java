package pro.iamgamer.core.database.repository;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.google.inject.persist.Transactional;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import pro.iamgamer.core.model.User;
import pro.iamgamer.core.security.PasswordUtils;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.DocumentCrud;


/**
 * Created by Sergey Kobets on 08.11.2015.
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class UserRepository implements DocumentCrud<User> {

    public ORID register(User user, String password) {
        final ODocument entries = create();
        entries.field("loginName", user.getLoginName(), OType.STRING);

        final byte[] salt = PasswordUtils.randomSalt();
        final byte[] passwordHash = PasswordUtils.hash(password.toCharArray(), salt);
        entries.field("salt", salt, OType.BINARY);
        entries.field("password", passwordHash, OType.BINARY);

        final ODocument save = save(entries);
        return save.getIdentity();
    }
}
