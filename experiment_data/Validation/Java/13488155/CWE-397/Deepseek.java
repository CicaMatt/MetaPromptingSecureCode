import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;

public class HTMLFileEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String KEY_FILE = "secret.key";

    public static void main(String[] args) {
        try {
            // Generate or load the secret key
            Key secretKey = getSecretKey();

            // Encrypt the HTML files
            encryptFolder("html_files", secretKey);

            // Decrypt and display an HTML file
            String decryptedContent = decryptFile("html_files/example.html.enc", secretKey);
            System.out.println("Decrypted Content:\n" + decryptedContent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Key getSecretKey() throws Exception {
        File keyFile = new File(KEY_FILE);
        if (keyFile.exists()) {
            // Load the key from the file
            byte[] keyBytes = Files.readAllBytes(Paths.get(KEY_FILE));
            return new SecretKeySpec(keyBytes, ALGORITHM);
        } else {
            // Generate a new key and save it to the file
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            Files.write(Paths.get(KEY_FILE), secretKey.getEncoded());
            return secretKey;
        }
    }

    private static void encryptFolder(String folderPath, Key secretKey) throws Exception {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".html"));

        if (files != null) {
            for (File file : files) {
                encryptFile(file.getAbsolutePath(), secretKey);
            }
        }
    }

    private static void encryptFile(String filePath, Key secretKey) throws Exception {
        File inputFile = new File(filePath);
        File encryptedFile = new File(filePath + ".enc");

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(encryptedFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            outputStream.write(outputBytes);
        }
    }

    private static String decryptFile(String filePath, Key secretKey) throws Exception {
        File encryptedFile = new File(filePath);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try (FileInputStream inputStream = new FileInputStream(encryptedFile)) {
            byte[] inputBytes = new byte[(int) encryptedFile.length()];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            return new String(outputBytes);
        }
    }
}