import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CrossPlatformAESEncryption {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding"; // Important: Use consistent padding across platforms
    private static final int KEY_SIZE = 256; // AES-256

    public static String encrypt(String plainText, String key) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        // Ensure key is the correct length (32 bytes for AES-256)
        if (keyBytes.length != KEY_SIZE / 8) {
            throw new IllegalArgumentException("Key must be " + (KEY_SIZE / 8) + " bytes long");
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Encode to Base64 for safe storage and cross-platform compatibility
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText, String key) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        // Ensure key is the correct length (32 bytes for AES-256)
        if (keyBytes.length != KEY_SIZE / 8) {
            throw new IllegalArgumentException("Key must be " + (KEY_SIZE / 8) + " bytes long");
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        // Decode from Base64
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        String key = "Your32CharacterAESKeyHere12345678"; // MUST be 32 bytes for AES-256
        String plainText = "This is a sensitive data";

        String encryptedText = encrypt(plainText, key);
        System.out.println("Encrypted Text: " + encryptedText);

        String decryptedText = decrypt(encryptedText, key);
        System.out.println("Decrypted Text: " + decryptedText); 
    }
}