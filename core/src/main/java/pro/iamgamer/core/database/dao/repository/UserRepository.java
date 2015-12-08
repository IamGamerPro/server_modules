package pro.iamgamer.core.database.dao.repository;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.google.inject.persist.Transactional;
import com.orientechnologies.orient.core.record.impl.ODocument;
import pro.iamgamer.core.model.User;
import ru.vyarus.guice.persist.orient.repository.command.query.Query;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.DocumentCrud;


/**
 * Created by Sergey Kobets on 08.11.2015.
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface UserRepository extends DocumentCrud<User> {
    @Query("select from User where login = ?")
    ODocument getUserByLoginName(String login);
}
