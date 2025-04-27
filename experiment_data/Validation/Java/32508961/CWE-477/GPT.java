import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESOpenSSLEncryption {

    private static final int KEY_SIZE = 256;
    private static final int ITERATIONS = 10000;
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16;

    public static void main(String[] args) throws Exception {
        String password = "testpass"; // Shared pass phrase
        String plainText = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        // Generate a random salt
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Initialize cipher for encryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        // Encrypt the message
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Concatenate salt, iv, and cipherText
        byte[] output = new byte[salt.length + iv.length + cipherText.length];
        System.arraycopy("Salted__".getBytes(StandardCharsets.UTF_8), 0, output, 0, 8); // Appending "Salted__" to match OpenSSL's magic bytes
        System.arraycopy(salt, 0, output, 8, salt.length);
        System.arraycopy(iv, 0, output, 8 + salt.length, iv.length);
        System.arraycopy(cipherText, 0, output, 8 + salt.length + iv.length, cipherText.length);

        // Encode to Base64
        String encryptedData = Base64.getEncoder().encodeToString(output);

        // Output the encrypted data
        System.out.println(encryptedData);
    }
}