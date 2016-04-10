package pro.iamgamer.auth.mongo.imp;

import io.vertx.core.VertxException;
import io.vertx.ext.auth.User;
import pro.iamgamer.auth.mongo.HashSaltStyle;
import pro.iamgamer.auth.mongo.HashStrategy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class DefaultHashStrategy implements HashStrategy {
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    private HashSaltStyle saltStyle;
    // Used only if SaltStyle#External is used
    private String externalSalt;

    public DefaultHashStrategy() {
        saltStyle = HashSaltStyle.COLUMN;
    }

    public DefaultHashStrategy(String externalSalt) {
        saltStyle = HashSaltStyle.EXTERNAL;
        this.externalSalt = externalSalt;
    }

    @Override
    public String computeHash(String password, User user) {
        switch (saltStyle) {
            case NO_SALT:
                return password;
            case COLUMN:
            case EXTERNAL:
                String salt = getSalt(user);
                return computeHash(password, salt, "SHA-512");
            default:
                throw new UnsupportedOperationException("Not existing, saltstyle " + saltStyle);
        }
    }

    @Override
    public String getStoredPwd(User user) {
        return ((MongoUser) user).getPassword();
    }

    @Override
    public String getSalt(User user) {
        switch (saltStyle) {
            case NO_SALT:
                return null;
            case COLUMN:
                return ((MongoUser) user).getSalt();
            case EXTERNAL:
                return externalSalt;
            default:
                throw new UnsupportedOperationException("Not existing, saltstyle " + saltStyle);
        }

    }

    @Override
    public void setSaltStyle(HashSaltStyle saltStyle) {
        this.saltStyle = saltStyle;
    }

    @Override
    public HashSaltStyle getSaltStyle() {
        return saltStyle;
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

    public static String generateSalt() {
        final Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        return bytesToHex(salt);
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

    @Override
    public void setExternalSalt(String externalSalt) {
        this.externalSalt = externalSalt;
    }

}
