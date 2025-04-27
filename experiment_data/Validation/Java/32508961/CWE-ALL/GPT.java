import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OpenSSLEncryption {

    public static void main(String[] args) {
        try {
            // Load password from a secure location (e.g., configuration file, environment variable)
            String password = System.getenv("ENCRYPTION_PASSWORD");
            
            // Load data to be encrypted from a file
            String data = new String(Files.readAllBytes(Paths.get("t.txt")), StandardCharsets.UTF_8);

            // Generate a random salt
            byte[] salt = new byte[8];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(salt);

            // PBKDF2 with SHA-256 to derive a secure AES key
            int iterations = 10000;
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            
            // AES Encryption with CBC mode and PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16]; // 128-bit IV
            secureRandom.nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Prepend "Salted__" and the salt to the IV and ciphertext
            byte[] saltPrefix = "Salted__".getBytes(StandardCharsets.UTF_8);
            byte[] combined = new byte[saltPrefix.length + salt.length + iv.length + cipherText.length];
            System.arraycopy(saltPrefix, 0, combined, 0, saltPrefix.length);
            System.arraycopy(salt, 0, combined, saltPrefix.length, salt.length);
            System.arraycopy(iv, 0, combined, saltPrefix.length + salt.length, iv.length);
            System.arraycopy(cipherText, 0, combined, saltPrefix.length + salt.length + iv.length, cipherText.length);

            // Base64 encode the final output similar to OpenSSL
            String encryptedOutput = Base64.getEncoder().encodeToString(combined);
            System.out.println("Encrypted Output: " + encryptedOutput);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | java.io.IOException e) {
            // Handle specific exceptions and report errors where necessary
            e.printStackTrace();
        }
    }
}