import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

public class SecurePasswordHandling {

    // Simulate a secure key for encryption (in practice, use a secure key management system)
    private static final String SECRET_KEY = "ThisIsASecretKey";

    public static void main(String[] args) {
        // Simulate a password input
        String password = "super_password";

        // Convert password to byte array
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        // Encrypt the password
        byte[] encryptedPassword = encrypt(passwordBytes);

        // Clear the original password from memory
        Arrays.fill(passwordBytes, (byte) 0);

        // Simulate sending the encrypted password to the backend
        sendToBackend(encryptedPassword);

        // Clear the encrypted password from memory after use
        Arrays.fill(encryptedPassword, (byte) 0);
    }

    private static byte[] encrypt(byte[] data) {
        try {
            Key key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    private static void sendToBackend(byte[] encryptedPassword) {
        // Simulate secure communication with the backend using TLS
        // In practice, use HTTPS with proper certificate validation
        String base64EncodedPassword = Base64.getEncoder().encodeToString(encryptedPassword);
        System.out.println("Sending encrypted password to backend: " + base64EncodedPassword);

        // Simulate backend decryption (for demonstration purposes)
        byte[] decryptedPassword = decrypt(Base64.getDecoder().decode(base64EncodedPassword));
        System.out.println("Decrypted password: " + new String(decryptedPassword, StandardCharsets.UTF_8));

        // Clear the decrypted password from memory
        Arrays.fill(decryptedPassword, (byte) 0);
    }

    private static byte[] decrypt(byte[] encryptedData) {
        try {
            Key key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}