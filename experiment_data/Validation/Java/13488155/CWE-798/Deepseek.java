import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecureHTMLAccess {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String ENCRYPTED_FOLDER = "encrypted_html";
    private static final String DECRYPTED_FOLDER = "decrypted_html";

    // Generate a secret key (this should be securely stored and not hard-coded)
    private static Key generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // 128-bit key
        return keyGen.generateKey();
    }

    // Encrypt a file
    private static void encryptFile(File inputFile, File outputFile, Key secretKey) throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile, secretKey);
    }

    // Decrypt a file
    private static void decryptFile(File inputFile, File outputFile, Key secretKey) throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile, secretKey);
    }

    private static void doCrypto(int cipherMode, File inputFile, File outputFile, Key secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(cipherMode, secretKey);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);
            outputStream.write(outputBytes);
        }
    }

    // Encrypt all HTML files in a folder
    public static void encryptFolder(String folderPath, Key secretKey) throws Exception {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".html"));

        if (files != null) {
            for (File file : files) {
                File encryptedFile = new File(ENCRYPTED_FOLDER, file.getName());
                encryptFile(file, encryptedFile, secretKey);
            }
        }
    }

    // Decrypt all HTML files in the encrypted folder
    public static void decryptFolder(Key secretKey) throws Exception {
        File folder = new File(ENCRYPTED_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".html"));

        if (files != null) {
            for (File file : files) {
                File decryptedFile = new File(DECRYPTED_FOLDER, file.getName());
                decryptFile(file, decryptedFile, secretKey);
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Generate a secret key (in a real-world scenario, this should be securely stored)
            Key secretKey = generateKey();

            // Encrypt the HTML files in the specified folder
            encryptFolder("html_files", secretKey);

            // Decrypt the HTML files for client access
            decryptFolder(secretKey);

            System.out.println("Encryption and decryption completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}