import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

// Custom exception class for encryption errors
class EncryptionException extends Exception {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class DataEncryptionUtil {

    // Method to encrypt data using AES-256
    public static String encryptData(String data, SecretKey secretKey) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Failed to encrypt data", e);
        }
    }

    // Method to decrypt data using AES-256
    public static String decryptData(String encryptedData, SecretKey secretKey) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Failed to decrypt data", e);
        }
    }

    // Generate a SecretKey for AES-256 (for demonstration; in practice, securely store/retrieve keys)
    public static SecretKey generateSecretKey() throws EncryptionException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Failed to generate secret key", e);
        }
    }

    public static void main(String[] args) {
        try {
            SecretKey secretKey = generateSecretKey();
            String originalData = "Sensitive data to encrypt";
            String encryptedData = encryptData(originalData, secretKey);
            System.out.println("Encrypted Data: " + encryptedData);

            String decryptedData = decryptData(encryptedData, secretKey);
            System.out.println("Decrypted Data: " + decryptedData);
        } catch (EncryptionException e) {
            // Apply structured logging for the exception
            System.err.println("Encryption error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}