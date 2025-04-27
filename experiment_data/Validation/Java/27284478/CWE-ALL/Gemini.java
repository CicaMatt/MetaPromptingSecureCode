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
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256"; // Strong, standard algorithm

    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (password == null || password.isEmpty() || salt == null || salt.length == 0) {
            throw new IllegalArgumentException("Password and salt cannot be null or empty.");
        }

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();
        // Clear the sensitive data from memory
        Arrays.fill(spec.getPassword(), ' ');

        return Base64.getEncoder().encodeToString(hash);
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Example usage (Salt should be stored securely, e.g., database)
        byte[] salt = generateSalt(); //  In real app, retrieve salt from storage.
        String password = "user_provided_password";  //  Get password from user input.

        try {
            String hashedPassword = hashPassword(password, salt);
            System.out.println("Hashed Password: " + hashedPassword);
            // ... store salt and hashedPassword securely ...

        } finally {
            // Clear the sensitive data from memory
            Arrays.fill(password.toCharArray(), ' '); 
        }
    }
}