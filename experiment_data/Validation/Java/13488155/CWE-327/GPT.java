import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class FileEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256; // AES-256 for strong encryption
    private static final int GCM_IV_LENGTH = 12; // Recommended IV size for GCM
    private static final int GCM_TAG_LENGTH = 16;

    public static void main(String[] args) {
        try {
            // Generate and save your key securely during deployment or startup (not hardcoded here)
            SecretKey secretKey = generateKey();

            String folderPath = "path/to/html/files";
            File folder = new File(folderPath);

            // Encrypt all HTML files in the folder
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".html"));
            if (files != null) {
                for (File file : files) {
                    encryptFile(secretKey, file.getPath(), file.getPath() + ".enc");
                    Files.delete(Paths.get(file.getPath()));  // Optionally remove original file
                }
            }

            // For decrypting, an example here
            String encryptedFile = "path/to/encrypted/file.html.enc";
            String decryptedOutput = "path/to/decrypted/file.html";
            decryptFile(secretKey, encryptedFile, decryptedOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE, new SecureRandom());
        return keyGenerator.generateKey();
    }

    private static void encryptFile(SecretKey key, String inputFile, String outputFile) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(iv); // Write IV at the beginning of the file
            byte[] inputBuffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(inputBuffer)) != -1) {
                byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
                if (outputBuffer != null) {
                    outputStream.write(outputBuffer);
                }
            }
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                outputStream.write(outputBytes);
            }
        }
    }

    private static void decryptFile(SecretKey key, String inputFile, String outputFile) throws Exception {
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            byte[] iv = new byte[GCM_IV_LENGTH];
            inputStream.read(iv); // Read the IV from the file

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            byte[] inputBuffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(inputBuffer)) != -1) {
                byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
                if (outputBuffer != null) {
                    outputStream.write(outputBuffer);
                }
            }
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                outputStream.write(outputBytes);
            }
        }
    }
}