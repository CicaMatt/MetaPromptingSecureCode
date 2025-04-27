import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;

public class FileEncryptorDecryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void main(String[] args) {
        try {
            // Generate a secret key for encryption and decryption
            Key secretKey = generateKey();

            // Path to the folder containing HTML files
            String folderPath = "path/to/your/html/folder";
            String encryptedFolderPath = "path/to/encrypted/folder";

            // Encrypt all HTML files in the folder
            encryptFolder(folderPath, encryptedFolderPath, secretKey);

            // Decrypt a specific HTML file for use in the application
            String decryptedContent = decryptFile(encryptedFolderPath + "/example.html", secretKey);
            System.out.println("Decrypted Content:\n" + decryptedContent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Key generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // 128-bit key size
        SecretKey secretKey = keyGen.generateKey();
        return secretKey;
    }

    private static void encryptFolder(String inputFolderPath, String outputFolderPath, Key secretKey) throws Exception {
        File inputFolder = new File(inputFolderPath);
        File[] files = inputFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".html")) {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    byte[] encryptedContent = encrypt(fileContent, secretKey);

                    // Save encrypted file to the output folder
                    String encryptedFilePath = outputFolderPath + "/" + file.getName();
                    Files.write(Paths.get(encryptedFilePath), encryptedContent);
                }
            }
        }
    }

    private static byte[] encrypt(byte[] data, Key secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private static String decryptFile(String filePath, Key secretKey) throws Exception {
        byte[] encryptedContent = Files.readAllBytes(Paths.get(filePath));
        byte[] decryptedContent = decrypt(encryptedContent, secretKey);
        return new String(decryptedContent);
    }

    private static byte[] decrypt(byte[] data, Key secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
}