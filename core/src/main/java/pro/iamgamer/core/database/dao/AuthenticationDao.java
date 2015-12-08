package pro.iamgamer.core.database.dao;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import pro.iamgamer.core.database.dao.repository.UserRepository;
import pro.iamgamer.core.model.User;
import pro.iamgamer.core.security.PasswordUtils;
import ru.vyarus.guice.persist.orient.db.PersistentContext;

import javax.inject.Inject;


/**
 * Created by sergey.kobets on 08.12.2015.
 */
public class AuthenticationDao {
    @Inject
    PersistentContext<ODatabaseDocumentTx> tx;
    @Inject
    UserRepository userRepository;

    private final static String VERSION = "0.0";

    public ORID register(User user, String password) {
        final ODocument entries = userRepository.create();
        entries.field("login", user.getLogin(), OType.STRING);

        final byte[] salt = PasswordUtils.randomSalt();
        final byte[] passwordHash = PasswordUtils.hash(password.toCharArray(), salt);
        entries.field("salt", salt, OType.BINARY);
        entries.field("password", passwordHash, OType.BINARY);
        entries.field("version", VERSION);

        final ODocument save = userRepository.save(entries);
        return save.getIdentity();
    }

    public User login(String login, String pwd){

        final User user = tx.<User>doWithoutTransaction(db -> {
            OSQLSynchQuery<OResultSet> query = new OSQLSynchQuery<>("select from User where login = " + "?");
            final OResultSet execute = db.command(query).<OResultSet>execute(login);
            return null;
        });

        return null;
    }


}
