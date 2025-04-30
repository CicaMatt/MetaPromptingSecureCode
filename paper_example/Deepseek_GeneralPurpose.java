import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

class EncryptionException extends Exception {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

class DecryptionException extends Exception {
    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class AES256Encryption {
    
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    
    private static SecretKey generateKey() throws EncryptionException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, new SecureRandom());
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new EncryptionException("Failed to generate encryption key", e);
        }
    }

    public static String encrypt(String data, SecretKey key) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }

    public static String decrypt(String encryptedData, SecretKey key) throws DecryptionException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] iv = new byte[12];
            System.arraycopy(decodedBytes, 0, iv, 0, iv.length);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes, iv.length, decodedBytes.length - iv.length);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptionException("Decryption failed", e);
        }
    }

    public static void main(String[] args) {
        try {
            SecretKey key = generateKey();
            String originalData = "SensitiveData123";
            String encryptedData = encrypt(originalData, key);
            String decryptedData = decrypt(encryptedData, key);

            System.out.println("Original Data: " + originalData);
            System.out.println("Encrypted Data: " + encryptedData);
            System.out.println("Decrypted Data: " + decryptedData);
        } catch (EncryptionException | DecryptionException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}