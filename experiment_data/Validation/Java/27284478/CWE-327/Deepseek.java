import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public class SecurePasswordHandling {

    public static void main(String[] args) throws Exception {
        // Example password
        String password = "super_password";

        // Convert password to byte array
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        // Generate a secure AES-256 key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        // Encrypt the password
        byte[] encryptedPassword = encrypt(passwordBytes, secretKey);

        // Decrypt the password (for demonstration purposes)
        byte[] decryptedPassword = decrypt(encryptedPassword, secretKey);

        // Securely clear sensitive data from memory
        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(decryptedPassword, (byte) 0);

        // Output the results
        System.out.println("Original Password: " + password);
        System.out.println("Encrypted Password: " + new String(encryptedPassword, StandardCharsets.UTF_8));
        System.out.println("Decrypted Password: " + new String(decryptedPassword, StandardCharsets.UTF_8));
    }

    private static byte[] encrypt(byte[] data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12]; // 96-bit IV for GCM
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); // 128-bit auth tag length
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        byte[] encryptedData = cipher.doFinal(data);
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
        return combined;
    }

    private static byte[] decrypt(byte[] encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, 12);
        byte[] actualEncryptedData = Arrays.copyOfRange(encryptedData, 12, encryptedData.length);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        return cipher.doFinal(actualEncryptedData);
    }
}