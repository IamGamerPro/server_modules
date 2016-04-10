package pro.iamgamer.auth.mongo.imp;

import io.vertx.ext.auth.User;
import pro.iamgamer.auth.mongo.HashStrategy;
import pro.iamgamer.core.security.PasswordUtils;

public class DefaultHashStrategy implements HashStrategy {
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

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

    private static String bytesToHex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int x = 0xFF & bytes[i];
            chars[i * 2] = HEX_CHARS[x >>> 4];
            chars[1 + i * 2] = HEX_CHARS[0x0F & x];
        }
        return new String(chars);
    }

}
