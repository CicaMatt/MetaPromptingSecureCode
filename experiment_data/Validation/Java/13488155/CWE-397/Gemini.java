import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HTMLFileEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String KEY_STRING = "YourSecretKey123"; // Replace with a strong, randomly generated key

    public static void encryptFile(Path inputFile, Path outputFile, SecretKey key) throws IOException, CryptoException {
        try (
                InputStream inputStream = Files.newInputStream(inputFile);
                OutputStream outputStream = Files.newOutputStream(outputFile);
                Cipher cipher = Cipher.getInstance(ALGORITHM)
        ) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
            }
        }
    }


    public static void decryptFile(Path inputFile, Path outputFile, SecretKey key) throws IOException, CryptoException {

        try (
                InputStream inputStream = Files.newInputStream(inputFile);
                OutputStream outputStream = Files.newOutputStream(outputFile);
                Cipher cipher = Cipher.getInstance(ALGORITHM)

        ) {
            cipher.init(Cipher.DECRYPT_MODE, key);
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }




    public static void main(String[] args) {
        try {
            SecretKey key = new SecretKeySpec(KEY_STRING.getBytes(), ALGORITHM);

            // Example usage:
            Path inputFile = Paths.get("path/to/your/html/file.html"); // Replace with your input file path
            Path encryptedFile = Paths.get("path/to/encrypted/file.encrypted"); // Replace with your encrypted file path
            Path decryptedFile = Paths.get("path/to/decrypted/file.html"); // Replace with your decrypted file path



            encryptFile(inputFile, encryptedFile, key);
            decryptFile(encryptedFile, decryptedFile, key);


            System.out.println("File encrypted and decrypted successfully!");

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
            e.printStackTrace();  // Log or handle more appropriately
        } catch (CryptoException e) {
            System.err.println("Cryptography Error: " + e.getMessage());
            e.printStackTrace();  // Log or handle more appropriately
        }

    }
}

class CryptoException extends Exception {
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(String message) {
        super(message);
    }
}