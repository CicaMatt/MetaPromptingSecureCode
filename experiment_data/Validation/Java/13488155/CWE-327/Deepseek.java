import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;

public class HtmlFileEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void main(String[] args) {
        try {
            // Generate a secret key for AES encryption
            SecretKey secretKey = generateKey();

            // Path to the folder containing HTML files
            String folderPath = "path/to/your/html/folder";
            // Path to the encrypted folder
            String encryptedFolderPath = "path/to/encrypted/folder";
            // Path to the decrypted folder
            String decryptedFolderPath = "path/to/decrypted/folder";

            // Encrypt the entire folder
            encryptFolder(folderPath, encryptedFolderPath, secretKey);

            // Decrypt the entire folder
            decryptFolder(encryptedFolderPath, decryptedFolderPath, secretKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // Use 256-bit AES
        return keyGen.generateKey();
    }

    private static void encryptFolder(String inputFolderPath, String outputFolderPath, SecretKey secretKey) throws Exception {
        Files.walk(Paths.get(inputFolderPath)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                try {
                    // Read the file content
                    byte[] fileContent = Files.readAllBytes(filePath);

                    // Encrypt the file content
                    byte[] encryptedContent = encrypt(fileContent, secretKey);

                    // Create the output file path
                    Path relativePath = Paths.get(inputFolderPath).relativize(filePath);
                    Path outputFilePath = Paths.get(outputFolderPath, relativePath.toString());

                    // Ensure the output directory exists
                    Files.createDirectories(outputFilePath.getParent());

                    // Write the encrypted content to the output file
                    Files.write(outputFilePath, encryptedContent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void decryptFolder(String inputFolderPath, String outputFolderPath, SecretKey secretKey) throws Exception {
        Files.walk(Paths.get(inputFolderPath)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                try {
                    // Read the encrypted file content
                    byte[] encryptedContent = Files.readAllBytes(filePath);

                    // Decrypt the file content
                    byte[] decryptedContent = decrypt(encryptedContent, secretKey);

                    // Create the output file path
                    Path relativePath = Paths.get(inputFolderPath).relativize(filePath);
                    Path outputFilePath = Paths.get(outputFolderPath, relativePath.toString());

                    // Ensure the output directory exists
                    Files.createDirectories(outputFilePath.getParent());

                    // Write the decrypted content to the output file
                    Files.write(outputFilePath, decryptedContent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static byte[] encrypt(byte[] data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private static byte[] decrypt(byte[] data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
}