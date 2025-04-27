import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HTMLFileEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // Choose a secure mode
    private static final String KEY_STRING = "YourStrongPassword123"; // MUST be 16, 24, or 32 bytes for AES

    private static SecretKeySpec getKey() {
        byte[] keyBytes = KEY_STRING.getBytes();  //  For demonstration only. In a real application, use a proper key derivation function (KDF) like PBKDF2
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static void encryptDirectory(Path source, Path destination) throws Exception {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = source.relativize(file);
                Path destinationFile = destination.resolve(relativePath);
                Files.createDirectories(destinationFile.getParent());
                encryptFile(file, destinationFile);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void decryptDirectory(Path source, Path destination) throws Exception {
         Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = source.relativize(file);
                Path destinationFile = destination.resolve(relativePath);
                Files.createDirectories(destinationFile.getParent());
                decryptFile(file, destinationFile);
                return FileVisitResult.CONTINUE;
            }
        });
    }


    public static void encryptFile(Path source, Path destination) throws Exception {
        try (InputStream fis = Files.newInputStream(source);
             OutputStream fos = Files.newOutputStream(destination);
             CipherOutputStream cos = new CipherOutputStream(fos, getCipher(Cipher.ENCRYPT_MODE))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                cos.write(buffer, 0, length);
            }
        }
    }

    public static void decryptFile(Path source, Path destination) throws Exception {
        try (InputStream fis = Files.newInputStream(source);
             OutputStream fos = Files.newOutputStream(destination);
             CipherInputStream cis = new CipherInputStream(fis, getCipher(Cipher.DECRYPT_MODE))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = cis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }


    private static Cipher getCipher(int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(mode, getKey());
        return cipher;
    }


    public static void main(String[] args) throws Exception {
        Path sourceDirectory = Paths.get("html_files"); // Replace with your HTML directory
        Path encryptedDirectory = Paths.get("encrypted_html");
        Path decryptedDirectory = Paths.get("decrypted_html");


        //Encrypt
        encryptDirectory(sourceDirectory, encryptedDirectory);
        System.out.println("Encryption complete.");

        // Decrypt -  In your Swing app, you'd decrypt on demand.
        decryptDirectory(encryptedDirectory, decryptedDirectory);
        System.out.println("Decryption complete.");

       //Now access the decrypted files within your Java application.
    }
}