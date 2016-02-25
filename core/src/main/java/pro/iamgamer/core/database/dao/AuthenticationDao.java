package pro.iamgamer.core.database.dao;

/**
 * Created by sergey.kobets on 08.12.2015.
 */
public class AuthenticationDao {
    /*@Inject
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
    */
}
