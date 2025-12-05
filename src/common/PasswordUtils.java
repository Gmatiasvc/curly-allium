package common;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 512;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    private static final SecureRandom RAND = new SecureRandom();

    public static String getSalt() {
        byte[] salt = new byte[32];
        RAND.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }


    public static String hashPassword(String password, String salt) {
        char[] chars = password.toCharArray();
        byte[] bytes = Base64.getDecoder().decode(salt);

        PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);
        
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password", e);
        } finally {
            Arrays.fill(chars, '0');
        }
    }

    public static boolean verifyPassword(String providedPassword, String storedSalt, String storedHash) {
        String newHash = hashPassword(providedPassword, storedSalt);
        return newHash.equals(storedHash);
    }
}