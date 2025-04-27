import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;

public class SecureHTMLAccess {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void main(String[] args) {
        try {
            // Path to the encrypted HTML folder
            String encryptedFolderPath = "path/to/encrypted/html/folder";
            // Path to the secure configuration file containing the encryption key
            String configFilePath = "path/to/secure/config/file";

            // Read the encryption key from the secure configuration file
            String encryptionKey = new String(Files.readAllBytes(Paths.get(configFilePath))).trim();

            // Decrypt the HTML files
            decryptFolder(encryptedFolderPath, encryptionKey);

            // Now the HTML files are decrypted and can be accessed
            // You can proceed with your application logic here

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void decryptFolder(String folderPath, String encryptionKey) throws Exception {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    decryptFile(file, encryptionKey);
                }
            }
        }
    }

    private static void decryptFile(File inputFile, String encryptionKey) throws Exception {
        File outputFile = new File(inputFile.getAbsolutePath() + ".decrypted");

        Key key = new SecretKeySpec(encryptionKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(Base64.getDecoder().decode(inputBytes));
            outputStream.write(outputBytes);
        }

        // Optionally, replace the encrypted file with the decrypted file
        inputFile.delete();
        outputFile.renameTo(inputFile);
    }

    // Method to encrypt files (for initial setup)
    public static void encryptFile(File inputFile, String encryptionKey) throws Exception {
        File outputFile = new File(inputFile.getAbsolutePath() + ".encrypted");

        Key key = new SecretKeySpec(encryptionKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);
            outputStream.write(Base64.getEncoder().encode(outputBytes));
        }

        // Optionally, replace the original file with the encrypted file
        inputFile.delete();
        outputFile.renameTo(inputFile);
    }
}