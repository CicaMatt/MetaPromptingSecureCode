import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class OpenSSLEncryption {

    public static void main(String[] args) throws Exception {
        String password = "testpass";
        String plainText = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        // Generate a random 8-byte salt
        byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);

        // Derive the key and IV using PBKDF2 with HMAC-SHA256
        int iterations = 10000;
        int keyLength = 256;
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Generate IV
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        // Encrypt the data
        byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Concatenate "Salted__", salt, and encrypted data
        byte[] result = new byte[16 + encryptedData.length];
        System.arraycopy("Salted__".getBytes(StandardCharsets.UTF_8), 0, result, 0, 8);
        System.arraycopy(salt, 0, result, 8, 8);
        System.arraycopy(encryptedData, 0, result, 16, encryptedData.length);

        // Base64 encode the result
        String encryptedBase64 = Base64.getEncoder().encodeToString(result);
        System.out.println(encryptedBase64);
    }
}