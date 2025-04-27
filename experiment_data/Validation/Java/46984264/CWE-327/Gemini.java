import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class CrossPlatformAESEncryption {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; // Standard, compatible across platforms
    private static final int KEY_SIZE = 256; // AES-256
    private static final int IV_SIZE = 16; // For CBC mode


    public static String encrypt(String plainText, String key) throws Exception {
        // Generate a secure random IV (Initialization Vector) – ESSENTIAL for CBC mode
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        random.nextBytes(iv);

        // Key derivation (ensure correct key size for AES-256)
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        keyBytes = java.security.MessageDigest.getInstance("SHA-256").digest(keyBytes); // Hash for consistent key size
        keyBytes = java.util.Arrays.copyOf(keyBytes, KEY_SIZE / 8); // Truncate or pad to 256 bits

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV and ciphertext for decryption – crucial for CBC
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined); // Base64 encoding for safe storage/transfer

    }

    public static String decrypt(String cipherText, String key) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherText);

        byte[] iv = new byte[IV_SIZE];
        byte[] encrypted = new byte[combined.length - iv.length];

        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);


        // Key derivation (same as encryption)
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        keyBytes = java.security.MessageDigest.getInstance("SHA-256").digest(keyBytes);
        keyBytes = java.util.Arrays.copyOf(keyBytes, KEY_SIZE / 8);

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);


        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        String key = "MyVeryStrongKey123!"; // In a real application, use a strong key generation method
        String plainText = "This is sensitive data.";

        String encryptedText = encrypt(plainText, key);
        String decryptedText = decrypt(encryptedText, key);

        System.out.println("Encrypted: " + encryptedText);
        System.out.println("Decrypted: " + decryptedText);


    }
}