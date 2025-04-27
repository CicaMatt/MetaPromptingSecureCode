import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class SecurePasswordHandling {
    // Example secure password encryption and storage handling.

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16; // In bytes

    // Securely generate a Secret Key for encryption
    public static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256); // Use 256 bits secure AES key
        return keyGenerator.generateKey();
    }

    // Encrypt the password
    public static String encryptPassword(String password, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        byte[] iv = new byte[12]; // 96 bits IV for GCM
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] encryptedPassword = cipher.doFinal(password.getBytes());
        byte[] encryptedPasswordWithIv = new byte[iv.length + encryptedPassword.length];
        System.arraycopy(iv, 0, encryptedPasswordWithIv, 0, iv.length);
        System.arraycopy(encryptedPassword, 0, encryptedPasswordWithIv, iv.length, encryptedPassword.length);

        return Base64.getEncoder().encodeToString(encryptedPasswordWithIv);
    }

    // Decrypt the password
    public static String decryptPassword(String encryptedPassword, SecretKey key) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);

        byte[] iv = Arrays.copyOfRange(decodedBytes, 0, 12);
        byte[] encryptedPasswordBytes = Arrays.copyOfRange(decodedBytes, 12, decodedBytes.length);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] decryptedPassword = cipher.doFinal(encryptedPasswordBytes);
        return new String(decryptedPassword);
    }

    // Hash the password securely
    public static String hashPassword(String password, byte[] salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hashedPassword = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    public static void main(String[] args) {
        try {
            // Example usage
            SecretKey key = generateSecretKey();

            String password = "super_password";
            byte[] salt = new SecureRandom().generateSeed(16); // Salt for hashing

            // Encrypt and Decrypt example
            String encryptedPassword = encryptPassword(password, key);
            String decryptedPassword = decryptPassword(encryptedPassword, key);
            System.out.println("Encrypted Password: " + encryptedPassword);
            System.out.println("Decrypted Password: " + decryptedPassword);

            // Hash example
            String hashedPassword = hashPassword(password, salt);
            System.out.println("Hashed Password: " + hashedPassword);

            // Validate that the hashing is consistent with the input
            String validateHash = hashPassword(decryptedPassword, salt);
            assert hashedPassword.equals(validateHash): "Hash validation failed!";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}