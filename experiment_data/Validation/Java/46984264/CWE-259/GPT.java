import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    // Method to generate a new AES key
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // for AES-256
        return keyGen.generateKey();
    }

    // Method to generate a random IV
    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // Method to encrypt plain text
    public static String encrypt(String plainText, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Method to decrypt encrypted text
    public static String decrypt(String cipherText, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        try {
            // Generate Key and IV once and store them securely
            SecretKey key = generateAESKey();
            byte[] iv = generateIV();

            // Example Usage
            String originalData = "Sensitive Data to Encrypt";
            String encryptedData = encrypt(originalData, key, iv);
            System.out.println("Encrypted: " + encryptedData);

            String decryptedData = decrypt(encryptedData, key, iv);
            System.out.println("Decrypted: " + decryptedData);
            
            // Output the key and iv to be securely stored
            System.out.println("AES Key: " + Base64.getEncoder().encodeToString(key.getEncoded()));
            System.out.println("IV: " + Base64.getEncoder().encodeToString(iv));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}