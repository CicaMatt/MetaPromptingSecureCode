import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AES256CompatibleEncryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // ECB mode is used for simplicity, but consider using CBC or GCM in production

    public static String encrypt(String data, String key) throws Exception {
        if (key.length() != 32) {
            throw new IllegalArgumentException("Key must be 32 characters long for AES-256 encryption.");
        }

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, String key) throws Exception {
        if (key.length() != 32) {
            throw new IllegalArgumentException("Key must be 32 characters long for AES-256 decryption.");
        }

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        try {
            String key = "12345678901234567890123456789012"; // 32-character key for AES-256
            String originalData = "SensitiveData123";

            // Encrypt the data
            String encryptedData = encrypt(originalData, key);
            System.out.println("Encrypted Data: " + encryptedData);

            // Decrypt the data
            String decryptedData = decrypt(encryptedData, key);
            System.out.println("Decrypted Data: " + decryptedData);

        } catch (IllegalArgumentException e) {
            System.err.println("Key length error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during encryption/decryption: " + e.getMessage());
        }
    }
}