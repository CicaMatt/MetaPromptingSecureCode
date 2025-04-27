import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SecurePasswordHandling {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // User provides password during initial setup or login
        char[] userProvidedPassword = "user_provided_password".toCharArray(); 

        // Hash the password securely
        byte[] salt = generateSalt();
        byte[] hashedPassword = hashPassword(userProvidedPassword, salt);

        // Store the salt and hashed password (e.g., in a database)
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String encodedHashedPassword = Base64.getEncoder().encodeToString(hashedPassword);

        System.out.println("Salt: " + encodedSalt);
        System.out.println("Hashed Password: " + encodedHashedPassword);


        // During subsequent login attempts:
        char[] userEnteredPassword = "user_provided_password".toCharArray(); // User enters password
        byte[] retrievedSalt = Base64.getDecoder().decode(encodedSalt); // Retrieve salt from storage
        byte[] retrievedHashedPassword = Base64.getDecoder().decode(encodedHashedPassword); // Retrieve hashed password

        byte[] hashedEnteredPassword = hashPassword(userEnteredPassword, retrievedSalt);

        // Constant-time comparison to prevent timing attacks
        if (MessageDigest.isEqual(retrievedHashedPassword, hashedEnteredPassword)) {
            System.out.println("Authentication successful!");
        } else {
            System.out.println("Authentication failed!");
        }

        // Clear sensitive data from memory 
        Arrays.fill(userProvidedPassword, ' ');
        Arrays.fill(userEnteredPassword, ' ');
    }


    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, ' '); // Clear password from memory after use
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword(); 
        }

    }

}