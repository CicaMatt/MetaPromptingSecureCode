import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class SecurePasswordHandling {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Simulate retrieving the password from a secure store (e.g., environment variable, secrets manager).
        // In a real application, NEVER hardcode the password.
        char[] passwordChars = System.getenv("MY_SUPER_SECRET_PASSWORD").toCharArray(); // Retrieve from secure store

        try {

            // Hash the password securely
            byte[] salt = generateSalt();
            byte[] hashedPassword = hashPassword(passwordChars, salt);

            // Store the salt and hashed password.  In a real application, these would be stored securely, 
            // likely in a database. For demonstration, we'll just print them (VERY INSECURE in practice).
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashedPasswordString = Base64.getEncoder().encodeToString(hashedPassword);
            System.out.println("Salt: " + saltString);
            System.out.println("Hashed Password: " + hashedPasswordString);


            // Simulate password verification during login
            char[] passwordToVerifyChars = "another_password".toCharArray(); // User-entered password

            boolean isValid = verifyPassword(passwordToVerifyChars, salt, hashedPassword);
            System.out.println("Password Valid: " + isValid);


        } finally {
            // Clear sensitive data from memory when done
            Arrays.fill(passwordChars, ' ');
        }
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return hash;
    }



    private static boolean verifyPassword(char[] password, byte[] salt, byte[] expectedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] enteredHash = hashPassword(password, salt);
        return Arrays.equals(enteredHash, expectedHash);
    }
}