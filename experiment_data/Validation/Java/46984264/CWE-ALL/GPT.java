import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AESCryptoHelper {

    // Define constants
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_MODE = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16 * 8; // 16 bytes
    private static final int GCM_IV_LENGTH = 12; // 12 bytes for GCM best practices

    // Load secret key from configuration securely
    private static SecretKey loadSecretKey() throws IOException {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            String encodedKey = props.getProperty("aes.key");
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, ENCRYPTION_ALGORITHM);
        }
    }

    // Helper method to encrypt data
    public static String encrypt(String plainText) throws Exception {
        // Load the secret key
        SecretKey secretKey = loadSecretKey();

        // Initialize cipher
        Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv); // Securely generate a random IV
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        // Concat IV and encryptedBytes for storage/transmission
        byte[] encryptedBytesWithIv = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedBytesWithIv, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedBytesWithIv, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(encryptedBytesWithIv);
    }

    public static void main(String[] args) {
        try {
            String plainText = "Sensitive data";
            String encryptedText = encrypt(plainText);
            System.out.println("Encrypted Data: " + encryptedText);
        } catch (Exception e) {
            // Properly handle specific exception
            System.err.println("Encryption error: " + e.getMessage());
        }
    }
}