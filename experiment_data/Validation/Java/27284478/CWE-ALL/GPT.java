import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class SecurePasswordHandler {

    private static final int KEY_SIZE = 256; // AES-256
    private static final int T_LEN = 128; // Authentication tag length for GCM mode
    private static final SecureRandom secureRandom = new SecureRandom();

    // Generate a new AES key
    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE, secureRandom);
        return keyGenerator.generateKey();
    }

    // Encrypt the password using AES/GCM/NoPadding
    private static String encryptPassword(String password, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12]; // IV size for GCM
        secureRandom.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(T_LEN, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        byte[] encryptedPassword = cipher.doFinal(password.getBytes());
        
        // Prepend IV to encrypted data
        byte[] encryptedIVAndPassword = new byte[iv.length + encryptedPassword.length];
        System.arraycopy(iv, 0, encryptedIVAndPassword, 0, iv.length);
        System.arraycopy(encryptedPassword, 0, encryptedIVAndPassword, iv.length, encryptedPassword.length);

        return Base64.getEncoder().encodeToString(encryptedIVAndPassword);
    }

    // Example usage
    public static void main(String[] args) {
        try {
            // Generate a secret key for encryption. In practice, this key should be securely managed
            SecretKey key = generateKey();

            // Securely encrypt the password
            String password = "super_password"; // This should not be hard-coded in production
            String encryptedPassword = encryptPassword(password, key);

            System.out.println("Encrypted Password: " + encryptedPassword);

            // Securely delete the password array after use
            char[] passCharArray = password.toCharArray();
            java.util.Arrays.fill(passCharArray, '\0'); // Clear the password array

        } catch (Exception e) {
            System.err.println("Encryption error: " + e.getMessage());
        }
    }
}