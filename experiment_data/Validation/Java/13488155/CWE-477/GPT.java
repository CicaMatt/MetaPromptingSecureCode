import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

public class FileEncryptor {

    // Key size can be 128, 192, or 256 bits
    private static final int AES_KEY_SIZE = 256;

    public static void main(String[] args) {
        String folderPath = "path/to/html/files";
        String secretKey = generateSecretKey();

        try {
            encryptFolder(folderPath, secretKey);
            decryptFolder(folderPath, secretKey);  // To test decryption, you can uncomment this line.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generates a new AES key
    public static String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_KEY_SIZE, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }

    // Encrypts files in the specified folder
    public static void encryptFolder(String folderPath, String secretKey) throws Exception {
        Files.walk(Paths.get(folderPath))
            .filter(Files::isRegularFile)
            .forEach(path -> {
                try {
                    encryptFile(path.toString(), secretKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }

    // Decrypts files in the specified folder
    public static void decryptFolder(String folderPath, String secretKey) throws Exception {
        Files.walk(Paths.get(folderPath))
            .filter(Files::isRegularFile)
            .forEach(path -> {
                try {
                    decryptFile(path.toString(), secretKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }

    // Encrypts a single file
    public static void encryptFile(String filePath, String secretKeyStr) throws Exception {
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(secretKeyStr), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        byte[] encryptedContent = cipher.doFinal(fileContent);

        // Save the encrypted content
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(encryptedContent);
        }
    }

    // Decrypts a single file
    public static void decryptFile(String filePath, String secretKeyStr) throws Exception {
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(secretKeyStr), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] encryptedContent = Files.readAllBytes(Paths.get(filePath));
        byte[] decryptedContent = cipher.doFinal(encryptedContent);

        // Save the decrypted content
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(decryptedContent);
        }
    }
}