import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;
import java.util.Base64;

public class OpenSSLCompatibleEncryption {

    public static void main(String[] args) throws Exception {
        String password = "testpass"; // Shared passphrase
        String plainText = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        // Encrypt
        String encryptedData = encrypt(plainText, password);
        System.out.println("Encrypted Data: " + encryptedData);

        // Decrypt
        String decryptedData = decrypt(encryptedData, password);
        System.out.println("Decrypted Data: " + decryptedData);
    }

    public static String encrypt(String plainText, String password) throws Exception {
        // Generate a random salt
        byte[] salt = new byte[8];
        // In a real scenario, use SecureRandom to generate a random salt
        // new SecureRandom().nextBytes(salt);
        // For compatibility with OpenSSL, we use a fixed salt
        System.arraycopy("Salted__".getBytes(StandardCharsets.UTF_8), 0, salt, 0, 8);

        // Derive the key using PBKDF2
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Encrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Concatenate salt, iv, and cipherText
        byte[] encryptedBytes = new byte[16 + cipherText.length];
        System.arraycopy("Salted__".getBytes(StandardCharsets.UTF_8), 0, encryptedBytes, 0, 8);
        System.arraycopy(salt, 0, encryptedBytes, 8, 8);
        System.arraycopy(iv, 0, encryptedBytes, 16, iv.length);
        System.arraycopy(cipherText, 0, encryptedBytes, 16 + iv.length, cipherText.length);

        // Base64 encode the result
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, String password) throws Exception {
        // Base64 decode the input
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

        // Extract salt, iv, and cipherText
        byte[] salt = new byte[8];
        System.arraycopy(encryptedBytes, 8, salt, 0, 8);
        byte[] iv = new byte[16];
        System.arraycopy(encryptedBytes, 16, iv, 0, 16);
        byte[] cipherText = new byte[encryptedBytes.length - 32];
        System.arraycopy(encryptedBytes, 32, cipherText, 0, cipherText.length);

        // Derive the key using PBKDF2
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Decrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        byte[] plainTextBytes = cipher.doFinal(cipherText);

        return new String(plainTextBytes, StandardCharsets.UTF_8);
    }
}