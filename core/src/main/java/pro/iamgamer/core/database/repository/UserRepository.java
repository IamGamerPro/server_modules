package pro.iamgamer.core.database.repository;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.google.inject.persist.Transactional;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import pro.iamgamer.core.model.User;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.DocumentCrud;

/**
 * Created by Sergey Kobets on 08.11.2015.
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class UserRepository implements DocumentCrud<User> {

    public ORID register(User user){
        final ODocument entries = create();
        entries.field("loginName", user.getLoginName(), OType.STRING);
        entries.field("salt", user.getSalt(), OType.BINARY);
        entries.field("password", user.getPassword(), OType.BINARY);
        final ODocument save = save(entries);
        return save.getIdentity();
    }


}
