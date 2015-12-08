package pro.iamgamer.core.database.dao.repository;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.google.inject.persist.Transactional;
import pro.iamgamer.core.model.User;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.DocumentCrud;


/**
 * Created by Sergey Kobets on 08.11.2015.
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface UserRepository extends DocumentCrud<User> {
}
