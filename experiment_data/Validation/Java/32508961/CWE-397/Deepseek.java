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

public class OpenSSLEncryption {

    public static void main(String[] args) throws Exception {
        String password = "testpass";
        String toBeEncrypted = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        // Generate a random 8-byte salt
        byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);

        // Derive the key using PBKDF2
        int iterations = 10000; // OpenSSL uses 10000 iterations by default
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Encrypt the data
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] cipherText = cipher.doFinal(toBeEncrypted.getBytes(StandardCharsets.UTF_8));

        // Concatenate "Salted__", salt, and encrypted data
        byte[] saltedData = new byte[8 + salt.length + cipherText.length];
        System.arraycopy("Salted__".getBytes(StandardCharsets.UTF_8), 0, saltedData, 0, 8);
        System.arraycopy(salt, 0, saltedData, 8, salt.length);
        System.arraycopy(cipherText, 0, saltedData, 8 + salt.length, cipherText.length);

        // Base64 encode the result
        String encryptedData = Base64.getEncoder().encodeToString(saltedData);

        System.out.println("Encrypted Data: " + encryptedData);
    }
}