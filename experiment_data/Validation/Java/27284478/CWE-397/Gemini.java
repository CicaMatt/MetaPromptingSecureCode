import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SecurePasswordHandling {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final byte[] SALT = getSalt(); // Store salt securely, e.g., in a configuration file


    private static byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }


    public static byte[] hashPassword(char[] password) throws NoSuchAlgorithmException {
        PBEKeySpec spec = new PBEKeySpec(password, SALT, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE); // Clear password from memory
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();

        } catch (NoSuchAlgorithmException e) {
             throw e; // Re-throw after clearing the password
        } finally {
            spec.clearPassword();
        }
    }

    public static boolean verifyPassword(char[] password, byte[] hashedPassword) throws NoSuchAlgorithmException {
        byte[] testHash = hashPassword(password);
        Arrays.fill(password, Character.MIN_VALUE);
        return Arrays.equals(testHash, hashedPassword);
    }




    public static void main(String[] args) {
        char[] password = "super_password".toCharArray();

        try {
            byte[] hashedPassword = hashPassword(password);

            // Store hashedPassword securely, not the original password.

            // ... later, when verifying ...
            if (verifyPassword(password, hashedPassword)) {
                System.out.println("Password verified.");
            } else {
                System.out.println("Incorrect password.");
            }

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            // Handle the specific exception appropriately, e.g., log and exit
        }


    }
}