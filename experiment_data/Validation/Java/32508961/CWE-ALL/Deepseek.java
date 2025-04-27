import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.util.Base64;

public class OpenSSLCompatibleEncryption {

    public static void main(String[] args) throws Exception {
        // Input data to be encrypted
        String toBeEncrypted = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        // Password (should be securely stored and retrieved, not hard-coded)
        String password = "testpass";

        // Generate a random salt (16 bytes is a common size)
        byte[] salt = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        // Number of iterations for PBKDF2 (should be high enough to prevent brute-force attacks)
        int iterations = 10000;

        // Derive the key using PBKDF2
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Initialize the cipher in encryption mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);

        // Get the IV (initialization vector) used for encryption
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        // Encrypt the data
        byte[] cipherText = cipher.doFinal(toBeEncrypted.getBytes(StandardCharsets.UTF_8));

        // Combine salt, IV, and cipherText into a single byte array
        byte[] combined = new byte[8 + salt.length + iv.length + cipherText.length];
        System.arraycopy("Salted__".getBytes(StandardCharsets.UTF_8), 0, combined, 0, 8); // OpenSSL header
        System.arraycopy(salt, 0, combined, 8, salt.length); // Salt
        System.arraycopy(iv, 0, combined, 8 + salt.length, iv.length); // IV
        System.arraycopy(cipherText, 0, combined, 8 + salt.length + iv.length, cipherText.length); // Ciphertext

        // Base64 encode the combined data
        String encryptedData = Base64.getEncoder().encodeToString(combined);

        // Output the encrypted data
        System.out.println("Encrypted Data: " + encryptedData);
    }
}