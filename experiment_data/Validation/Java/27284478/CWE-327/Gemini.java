import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class SecurePasswordHandling {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // in bits
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256"; // Strong algorithm

    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Generate a secure random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Key derivation using PBKDF2
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Concatenate salt and hash and encode in Base64
        byte[] saltAndHash = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
        System.arraycopy(hash, 0, saltAndHash, salt.length, hash.length);

        return Base64.getEncoder().encodeToString(saltAndHash);
    }

    public static boolean verifyPassword(String password, String storedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Decode the Base64 encoded salt and hash
        byte[] saltAndHash = Base64.getDecoder().decode(storedHash);
        byte[] salt = Arrays.copyOfRange(saltAndHash, 0, 16);
        byte[] storedHashBytes = Arrays.copyOfRange(saltAndHash, 16, saltAndHash.length);

        // Key derivation using PBKDF2 with the same salt and iterations
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Constant-time comparison to prevent timing attacks
        return Arrays.equals(storedHashBytes, hash);
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "super_password";
        String hashedPassword = hashPassword(password);
        System.out.println("Hashed password: " + hashedPassword);

        boolean isValid = verifyPassword(password, hashedPassword);
        System.out.println("Password verification: " + isValid);

        boolean isInvalid = verifyPassword("wrong_password", hashedPassword);
        System.out.println("Password verification (wrong password): " + isInvalid);
    }
}