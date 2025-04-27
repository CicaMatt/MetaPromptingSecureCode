import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * A Java application that encrypts and decrypts HTML files using AES in GCM mode.
 * The secret key is stored securely outside the application code.
 */
public class SecureHtmlManager {

    private static final int KEY_SIZE = 256; // 256-bit AES key
    private static final int GCM_IV_LENGTH = 12; // IV size for GCM mode
    private static final int GCM_TAG_LENGTH = 16; // GCM authentication tag length

    /**
     * Encrypts the given file using a provided secret key, storing the encrypted
     * content and IV.
     */
    public void encryptFile(File inputFile, File encryptedFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(encryptedFile)) {
            fos.write(iv); // Write the IV at the beginning of the file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] encrypted = cipher.update(buffer, 0, bytesRead);
                if (encrypted != null) {
                    fos.write(encrypted);
                }
            }
            byte[] encryptedFinal = cipher.doFinal();
            if (encryptedFinal != null) {
                fos.write(encryptedFinal);
            }
        }
    }

    /**
     * Decrypts the given encrypted file using the provided secret key.
     */
    public void decryptFile(File encryptedFile, File decryptedFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        try (FileInputStream fis = new FileInputStream(encryptedFile);
             FileOutputStream fos = new FileOutputStream(decryptedFile)) {
            byte[] iv = new byte[GCM_IV_LENGTH];
            fis.read(iv, 0, iv.length);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] decrypted = cipher.update(buffer, 0, bytesRead);
                if (decrypted != null) {
                    fos.write(decrypted);
                }
            }
            byte[] decryptedFinal = cipher.doFinal();
            if (decryptedFinal != null) {
                fos.write(decryptedFinal);
            }
        }
    }

    /**
     * Loads the secret key from a secure storage location.
     */
    public SecretKey loadSecretKey(String keyFilePath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(keyFilePath).toPath());
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Generates a new AES secret key and stores it securely.
     */
    public void generateKey(String keyFilePath) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE);
        SecretKey key = keyGen.generateKey();
        Files.write(new File(keyFilePath).toPath(), key.getEncoded());
    }

    /**
     * Main entry for testing the encryption and decryption process.
     */
    public static void main(String[] args) {
        SecureHtmlManager manager = new SecureHtmlManager();
        try {
            String keyPath = "path/to/secure/keyfile.key";
            // Generate and save a new secret key (one-time operation)
            manager.generateKey(keyPath);

            SecretKey key = manager.loadSecretKey(keyPath);
            File htmlFile = new File("path/to/html/file.html");
            File encryptedFile = new File("path/to/encrypted/file.enc");
            File decryptedFile = new File("path/to/decrypted/file.html");

            // Encrypt the HTML file
            manager.encryptFile(htmlFile, encryptedFile, key);

            // Decrypt the HTML file back to a readable format
            manager.decryptFile(encryptedFile, decryptedFile, key);

            System.out.println("Encryption and decryption completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}