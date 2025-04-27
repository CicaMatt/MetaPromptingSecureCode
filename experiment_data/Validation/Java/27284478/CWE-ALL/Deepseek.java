import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class SecurePasswordHandler {

    private static final String ENCRYPTION_KEY = System.getenv("ENCRYPTION_KEY");
    private static final String ALGORITHM = "AES";

    private static SecretKeySpec generateKey() throws Exception {
        if (ENCRYPTION_KEY == null || ENCRYPTION_KEY.isEmpty()) {
            throw new IllegalStateException("Encryption key is not set.");
        }
        byte[] key = ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        return new SecretKeySpec(Arrays.copyOf(key, 16), ALGORITHM);
    }

    public static String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = generateKey();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = generateKey();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] combined = Base64.getDecoder().decode(encryptedData);
        byte[] iv = Arrays.copyOfRange(combined, 0, 12);
        byte[] encryptedBytes = Arrays.copyOfRange(combined, 12, combined.length);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static String handleSensitiveData(char[] password) throws Exception {
        String passwordStr = new String(password);
        String encryptedPassword = encrypt(passwordStr);
        Arrays.fill(password, '\0');
        return encryptedPassword;
    }

    public static void main(String[] args) {
        try {
            char[] password = "super_password".toCharArray();
            String encryptedPassword = handleSensitiveData(password);
            System.out.println("Encrypted Password: " + encryptedPassword);
            String decryptedPassword = decrypt(encryptedPassword);
            System.out.println("Decrypted Password: " + decryptedPassword);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
