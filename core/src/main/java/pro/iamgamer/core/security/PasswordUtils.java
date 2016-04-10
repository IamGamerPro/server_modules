package pro.iamgamer.core.security;

import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

public final class PasswordUtils {
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 512;
    private static final int RANDOM_PASSWORD_LENGTH = 12;
    private static final SecretKeyFactory skf = new SecretKeyFactoryHolder().getFactory();

    public static byte[] randomSalt() {
        byte[] salt = new byte[31];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        Arrays.fill(salt, Byte.MIN_VALUE);
        try {
            return skf.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } finally {
            spec.clearPassword();
        }
    }

    public static String generateRandomPassword() {
        return RandomStringUtils.random(RANDOM_PASSWORD_LENGTH, 0, 0, true, true);
    }

    private static class SecretKeyFactoryHolder{
        private final SecretKeyFactory secretKeyFactory;
        SecretKeyFactoryHolder() {
            try {
                secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        SecretKeyFactory getFactory(){
            return secretKeyFactory;
        }
    }
}
