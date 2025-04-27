import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class SecurePasswordHandling {

    private static final String ALGORITHM = "AES";
    private static SecretKey secretKey;

    static {
        try {
            // Generate a secret key for encryption
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(128); // 128-bit key
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Simulate user input for password
        System.out.print("Enter your password: ");
        char[] passwordChars = scanner.nextLine().toCharArray();

        // Convert char array to byte array for encryption
        byte[] passwordBytes = toBytes(passwordChars);

        // Encrypt the password
        byte[] encryptedPassword = encrypt(passwordBytes);
        System.out.println("Encrypted Password: " + Base64.getEncoder().encodeToString(encryptedPassword));

        // Decrypt the password (for demonstration purposes)
        byte[] decryptedPassword = decrypt(encryptedPassword);
        System.out.println("Decrypted Password: " + new String(decryptedPassword, StandardCharsets.UTF_8));

        // Clear sensitive data from memory
        clearArray(passwordChars);
        clearArray(passwordBytes);
        clearArray(decryptedPassword);
    }

    private static byte[] toBytes(char[] chars) {
        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            bytes[i * 2] = (byte) (chars[i] >> 8);
            bytes[i * 2 + 1] = (byte) chars[i];
        }
        return bytes;
    }

    private static byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    private static byte[] decrypt(byte[] encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    private static void clearArray(char[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                array[i] = '\0';
            }
        }
    }

    private static void clearArray(byte[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                array[i] = 0;
            }
        }
    }
}