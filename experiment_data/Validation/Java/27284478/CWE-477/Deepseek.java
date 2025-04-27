import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SecurePasswordHandling {

    public static void main(String[] args) throws Exception {
        // Example password
        String password = "super_password";

        // Convert password to byte array
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        // Generate a secret key for encryption
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // 128-bit key size
        SecretKey secretKey = keyGen.generateKey();

        // Encrypt the password
        byte[] encryptedPassword = encrypt(passwordBytes, secretKey);

        // Decrypt the password (for demonstration purposes)
        byte[] decryptedPassword = decrypt(encryptedPassword, secretKey);

        // Convert decrypted bytes back to String
        String decryptedPasswordString = new String(decryptedPassword, StandardCharsets.UTF_8);

        // Output the results
        System.out.println("Original Password: " + password);
        System.out.println("Encrypted Password: " + Base64.getEncoder().encodeToString(encryptedPassword));
        System.out.println("Decrypted Password: " + decryptedPasswordString);

        // Clear sensitive data from memory
        clearArray(passwordBytes);
        clearArray(encryptedPassword);
        clearArray(decryptedPassword);
    }

    private static byte[] encrypt(byte[] data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private static byte[] decrypt(byte[] encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

    private static void clearArray(byte[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                array[i] = 0;
            }
        }
    }
}