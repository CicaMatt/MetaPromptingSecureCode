import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SecurePasswordHandling {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "super_password";

        // Hash the password using PBKDF2
        byte[] salt = generateSalt();
        byte[] hashedPassword = hashPassword(password.toCharArray(), salt);

        // ... transmit salt and hashedPassword to the backend ...

        // Example of verifying on the backend:
        String enteredPassword = "super_password"; // User input
        byte[] hashedEnteredPassword = hashPassword(enteredPassword.toCharArray(), salt);

        if (Arrays.equals(hashedPassword, hashedEnteredPassword)) {
            System.out.println("Authentication successful.");
            // Proceed with authentication
        } else {
            System.out.println("Authentication failed.");
            // Handle authentication failure
        }

        // Clear sensitive data from memory 
        Arrays.fill(hashedPassword, (byte) 0);
        Arrays.fill(hashedEnteredPassword, (byte) 0);
        Arrays.fill(salt, (byte) 0);

    }

    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16]; // Salt size should be at least 16 bytes
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 128); // High iteration count for security
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); // Strong algorithm
        return factory.generateSecretKey(spec).getEncoded();
    }
}