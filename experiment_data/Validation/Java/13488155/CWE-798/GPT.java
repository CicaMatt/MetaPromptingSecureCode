import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureFileHandler {

    // Generate a random AES key
    public static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Using AES-256
        return keyGen.generateKey();
    }

    // Encrypt a file
    public static void encryptFile(Path sourceFilePath, Path destFilePath, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        try (FileInputStream fis = new FileInputStream(sourceFilePath.toFile());
             FileOutputStream fos = new FileOutputStream(destFilePath.toFile());
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    // Decrypt a file
    public static void decryptFile(Path encryptedFilePath, Path destFilePath, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        try (FileInputStream fis = new FileInputStream(encryptedFilePath.toFile());
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             FileOutputStream fos = new FileOutputStream(destFilePath.toFile())) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    // Save the secret key to a file
    public static void saveKey(SecretKey key, String fileName) throws IOException {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(encodedKey);
        }
    }

    // Load the secret key from a file
    public static SecretKey loadKey(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String encodedKey = reader.readLine();
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        }
    }

    public static void main(String[] args) {
        try {
            // Generate and save the AES key (do this once and securely store the key)
            SecretKey secretKey = generateSecretKey();
            saveKey(secretKey, "mySecretKey.key");

            // Load the AES key
            SecretKey key = loadKey("mySecretKey.key");

            // Encryption and decryption example
            Path sourcePath = Paths.get("example.html");
            Path encryptedPath = Paths.get("encrypted_example.enc");
            Path decryptedPath = Paths.get("decrypted_example.html");

            // Encrypt the file
            encryptFile(sourcePath, encryptedPath, key);

            // Optionally update the encrypted file here...

            // Decrypt the file
            decryptFile(encryptedPath, decryptedPath, key);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}