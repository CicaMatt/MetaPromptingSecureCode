import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class SecureHTMLAccess {

    private static final String PASSWORD = System.getenv("HTML_ENCRYPTION_PASSWORD"); // From environment variable
    private static final String SALT = System.getenv("HTML_ENCRYPTION_SALT"); // From environment variable
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public static void main(String[] args) throws Exception {
        if (PASSWORD == null || SALT == null) {
            System.err.println("Error: HTML_ENCRYPTION_PASSWORD and HTML_ENCRYPTION_SALT environment variables must be set.");
            System.exit(1);
        }

        String htmlFolderPath = "html_files"; // Replace with your actual folder path
        String encryptedFolderPath = "encrypted_html"; // Replace with your encrypted folder path

        // Encrypt or decrypt based on command-line arguments
        if (args.length > 0 && args[0].equals("encrypt")) {
            encryptFolder(htmlFolderPath, encryptedFolderPath);
        } else {
            String htmlFileName = "index.html"; // Replace with the HTML file you want to access
            String decryptedHTML = decryptFile(encryptedFolderPath + "/" + htmlFileName);
            System.out.println(decryptedHTML); // Display the decrypted HTML.  Use in your Swing application
        }
    }


    private static void encryptFolder(String sourceFolderPath, String destinationFolderPath) throws Exception {
        Files.walk(Paths.get(sourceFolderPath))
                .filter(Files::isRegularFile)
                .forEach(sourceFile -> {
                    try {
                        byte[] encryptedData = encryptFile(sourceFile);
                        Path destinationFile = Paths.get(destinationFolderPath, sourceFile.getFileName().toString());
                        Files.createDirectories(destinationFile.getParent()); // Create directories if needed
                        Files.write(destinationFile, encryptedData);
                    } catch (Exception e) {
                        System.err.println("Error encrypting file: " + sourceFile + ": " + e.getMessage());
                    }
                });
    }


    private static byte[] encryptFile(Path filePath) throws Exception {
        byte[] fileContent = Files.readAllBytes(filePath);
        return encrypt(fileContent);
    }

    private static String decryptFile(String encryptedFilePath) throws Exception {
        byte[] encryptedContent = Files.readAllBytes(Paths.get(encryptedFilePath));
        return decrypt(encryptedContent);

    }



    private static byte[] encrypt(byte[] data) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(PASSWORD.toCharArray(), SALT.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedData = cipher.doFinal(data);
        return Base64.getEncoder().encode(concatenate(iv, encryptedData));
    }


    private static String decrypt(byte[] encryptedData) throws Exception {

        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[16];
        byte[] ciphertext = new byte[decodedData.length - 16];
        System.arraycopy(decodedData, 0, iv, 0, iv.length);
        System.arraycopy(decodedData, iv.length, ciphertext, 0, ciphertext.length);



        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(PASSWORD.toCharArray(), SALT.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return new String(cipher.doFinal(ciphertext));
    }


    private static byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }



}