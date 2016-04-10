package pro.iamgamer.auth.mongo.imp;

import io.vertx.core.VertxException;
import io.vertx.ext.auth.User;
import pro.iamgamer.auth.mongo.HashStrategy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DefaultHashStrategy implements HashStrategy {
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    public DefaultHashStrategy() {
    }

    @Override
    public String computeHash(String password, User user) {
        String salt = getSalt(user);
        return computeHash(password, salt, "SHA-512");
    }

    @Override
    public String getStoredPwd(User user) {
        return ((MongoUser) user).getPassword();
    }

    @Override
    public String getSalt(User user) {
        return ((MongoUser) user).getSalt();
    }

    private String computeHash(String password, String salt, String algo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            String concat = (salt == null ? "" : salt) + password;
            byte[] bHash = md.digest(concat.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bHash);
        } catch (NoSuchAlgorithmException e) {
            throw new VertxException(e);
        }
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
