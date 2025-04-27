import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class SecurePasswordHandling {

    // Generate a secure key for encryption
    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Use 256-bit AES encryption
        return keyGen.generateKey();
    }

    // Encrypt the password using AES encryption
    private static byte[] encryptPassword(char[] password, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(new String(password).getBytes(StandardCharsets.UTF_8));
    }

    // Decrypt the password using AES encryption
    private static char[] decryptPassword(byte[] encryptedPassword, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(encryptedPassword);
        return new String(decryptedBytes, StandardCharsets.UTF_8).toCharArray();
    }

    // Hash the password using SHA-256 with a salt
    private static String hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hashedBytes = digest.digest(new String(password).getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    // Generate a random salt
    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static void main(String[] args) {
        try {
            // Example password
            char[] password = "super_password".toCharArray();

            // Generate a secure key
            SecretKey key = generateKey();

            // Encrypt the password
            byte[] encryptedPassword = encryptPassword(password, key);
            System.out.println("Encrypted Password: " + Base64.getEncoder().encodeToString(encryptedPassword));

            // Decrypt the password
            char[] decryptedPassword = decryptPassword(encryptedPassword, key);
            System.out.println("Decrypted Password: " + new String(decryptedPassword));

            // Generate a salt and hash the password
            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(password, salt);
            System.out.println("Hashed Password: " + hashedPassword);

            // Clear sensitive data from memory
            Arrays.fill(password, '\0');
            Arrays.fill(decryptedPassword, '\0');
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}