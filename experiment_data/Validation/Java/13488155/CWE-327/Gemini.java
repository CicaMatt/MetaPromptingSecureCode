import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.AESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureHTMLFiles {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int KEY_SIZE = 256; // Use 256-bit AES
    private static final String KEY = "YourStrongPasswordHereReplaceThis"; // WARNING: In a real application, NEVER hardcode the key like this. Use a secure key derivation and storage mechanism.
    private static final byte[] SALT = "SomeSaltValueHere".getBytes(); // Similarly, never hardcode the salt.


    public static void encryptFile(Path inputFile, Path outputFile) throws IOException, GeneralSecurityException {
        SecretKey key = generateKey(KEY.toCharArray(), SALT);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec iv = generateIv();
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        try (InputStream inputStream = Files.newInputStream(inputFile);
             OutputStream outputStream = new CipherOutputStream(Files.newOutputStream(outputFile), cipher)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }


    public static String decryptFile(Path inputFile) throws IOException, GeneralSecurityException {
        SecretKey key = generateKey(KEY.toCharArray(), SALT);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec iv = generateIv();
        cipher.init(Cipher.DECRYPT_MODE, key, iv);


        StringBuilder decryptedContent = new StringBuilder();
        try (InputStream inputStream = new CipherInputStream(Files.newInputStream(inputFile), cipher)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                decryptedContent.append(line).append("\n");
            }
        }
        return decryptedContent.toString();
    }


    private static SecretKey generateKey(char[] password, byte[] salt) throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        AESKeySpec keySpec = new AESKeySpec(password, salt, 65536, KEY_SIZE); // Use a high iteration count
        SecretKey secretKey = factory.generateSecret(keySpec);
        return secretKey;

    }

    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }



    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Path htmlFolder = Paths.get("html_files");  // Path to your HTML files
        Path encryptedFolder = Paths.get("encrypted_html");  // Path for encrypted files

        Files.createDirectories(encryptedFolder);

        // Encrypt all HTML files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(htmlFolder, "*.html")) {
            for (Path entry : stream) {
                Path encryptedFile = encryptedFolder.resolve(entry.getFileName());
                encryptFile(entry, encryptedFile);
                System.out.println("Encrypted: " + entry + " to " + encryptedFile);
            }
        }


        // Example of how to decrypt within your Swing application
        Path encryptedFileExample = encryptedFolder.resolve("index.html"); // Replace with desired filename
        String decryptedHTML = decryptFile(encryptedFileExample);
        System.out.println("Decrypted content:\n" + decryptedHTML);

        // ... Use the decryptedHTML in your Swing application (e.g., load into a JEditorPane or similar) ...

    }
}