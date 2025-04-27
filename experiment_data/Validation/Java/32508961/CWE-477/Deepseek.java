import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;
import java.util.Base64;

public class OpenSSLEncryption {

    public static void main(String[] args) throws Exception {
        String password = "testpass";
        String plainText = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        // Generate a random salt
        byte[] salt = new byte[8];
        java.security.SecureRandom.getInstanceStrong().nextBytes(salt);

        // Derive the key using PBKDF2 with HMAC-SHA256
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100, 256);
        SecretKeySpec secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        // Initialize the cipher in encryption mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        // Encrypt the plaintext
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Concatenate Salted__ + salt + iv + cipherText
        byte[] saltedHeader = "Salted__".getBytes(StandardCharsets.UTF_8);
        byte[] combined = new byte[saltedHeader.length + salt.length + iv.length + cipherText.length];
        System.arraycopy(saltedHeader, 0, combined, 0, saltedHeader.length);
        System.arraycopy(salt, 0, combined, saltedHeader.length, salt.length);
        System.arraycopy(iv, 0, combined, saltedHeader.length + salt.length, iv.length);
        System.arraycopy(cipherText, 0, combined, saltedHeader.length + salt.length + iv.length, cipherText.length);

        // Base64 encode the combined data
        String encryptedData = Base64.getEncoder().encodeToString(combined);
        System.out.println(encryptedData);
    }
}