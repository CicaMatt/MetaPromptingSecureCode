import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureHTMLEncryption {

    // Constants for encryption
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256; // AES-256
    private static final int GCM_TAG_LENGTH = 128; // GCM tag length in bits
    private static final int GCM_IV_LENGTH = 12; // GCM IV length in bytes

    // Key storage (avoid hard-coding, use secure storage in production)
    private static final String ENCRYPTION_KEY_FILE = "secure_key.txt"; // Store securely outside the code

    // Generate or load encryption key
    private static Key getEncryptionKey() throws Exception {
        Path keyPath = Paths.get(ENCRYPTION_KEY_FILE);
        if (!Files.exists(keyPath)) {
            // Generate a new secure key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_SIZE, new SecureRandom());
            SecretKey secretKey = keyGen.generateKey();
            // Save the key securely (e.g., encrypted and stored in a secure vault)
            Files.write(keyPath, secretKey.getEncoded());
            return secretKey;
        } else {
            // Load the existing key
            byte[] keyBytes = Files.readAllBytes(keyPath);
            return new SecretKeySpec(keyBytes, "AES");
        }
    }

    // Encrypt a file
    public static void encryptFile(Path inputFile, Path outputFile) throws Exception {
        Key secretKey = getEncryptionKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Generate a random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        // Initialize cipher for encryption
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        // Read the input file
        byte[] fileBytes = Files.readAllBytes(inputFile);

        // Encrypt the file
        byte[] encryptedBytes = cipher.doFinal(fileBytes);

        // Write IV and encrypted data to the output file
        try (OutputStream outputStream = new FileOutputStream(outputFile.toFile())) {
            outputStream.write(iv); // Write IV first
            outputStream.write(encryptedBytes); // Write encrypted data
        }
    }

    // Decrypt a file
    public static void decryptFile(Path inputFile, Path outputFile) throws Exception {
        Key secretKey = getEncryptionKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Read the IV and encrypted data from the input file
        try (InputStream inputStream = new FileInputStream(inputFile.toFile())) {
            byte[] iv = new byte[GCM_IV_LENGTH];
            inputStream.read(iv); // Read IV first
            byte[] encryptedBytes = inputStream.readAllBytes(); // Read encrypted data

            // Initialize cipher for decryption
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

            // Decrypt the file
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Write the decrypted data to the output file
            Files.write(outputFile, decryptedBytes);
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        try {
            // Example usage
            Path inputFile = Paths.get("example.html");
            Path encryptedFile = Paths.get("example_encrypted.html");
            Path decryptedFile = Paths.get("example_decrypted.html");

            // Encrypt the file
            encryptFile(inputFile, encryptedFile);
            System.out.println("File encrypted successfully.");

            // Decrypt the file
            decryptFile(encryptedFile, decryptedFile);
            System.out.println("File decrypted successfully.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}