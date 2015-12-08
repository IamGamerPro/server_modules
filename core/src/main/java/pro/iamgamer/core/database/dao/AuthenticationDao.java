package pro.iamgamer.core.database.dao;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import pro.iamgamer.core.database.dao.repository.UserRepository;
import pro.iamgamer.core.database.exceptions.IncorrectLoginOrPasswordException;
import pro.iamgamer.core.model.User;
import pro.iamgamer.core.security.PasswordUtils;
import ru.vyarus.guice.persist.orient.db.PersistentContext;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;


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

    public User login(String login, String password) {
        final Optional<ODocument> userByLoginName = Optional.ofNullable(userRepository.getUserByLoginName(login));
        if (userByLoginName.isPresent()) {
            final ODocument user = userByLoginName.get();
            final byte[] passwordInDb = user.field("password");
            final byte[] salt = user.field("salt");
            final byte[] hash = PasswordUtils.hash(password.toCharArray(), salt);
            final boolean equals = Arrays.equals(passwordInDb, hash);
            if (!equals) {
                throw new IncorrectLoginOrPasswordException();
            }
            final User result = new User(login);
            result.setId(user.getIdentity());
            result.setModVersion(user.getVersion());
            return result;
        } else {
            throw new IncorrectLoginOrPasswordException();
        }
    }
}
