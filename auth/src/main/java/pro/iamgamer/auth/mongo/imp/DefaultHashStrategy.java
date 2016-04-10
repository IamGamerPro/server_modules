package pro.iamgamer.auth.mongo.imp;

import io.vertx.ext.auth.User;
import pro.iamgamer.auth.mongo.HashStrategy;
import pro.iamgamer.core.security.PasswordUtils;

public class DefaultHashStrategy implements HashStrategy {
    public DefaultHashStrategy() {
    }

    @Override
    public byte[] computeHash(String password, User user) {
        byte[] salt = getSalt(user);
        return PasswordUtils.hash(password.toCharArray(), salt);
    }

    @Override
    public byte[] getStoredPwd(User user) {
        return ((MongoUser) user).getPassword();
    }

    @Override
    public byte[] getSalt(User user) {
        return ((MongoUser) user).getSalt();
    }
}
