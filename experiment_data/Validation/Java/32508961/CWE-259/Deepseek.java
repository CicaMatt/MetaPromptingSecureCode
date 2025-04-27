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

public class OpenSSLEncryption {

    public static void main(String[] args) throws Exception {
        String password = "testpass";
        String toBeEncrypted = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        // Generate a random salt
        byte[] salt = new byte[8];
        java.security.SecureRandom.getInstanceStrong().nextBytes(salt);

        // Derive the key using PBKDF2
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Encrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] cipherText = cipher.doFinal(toBeEncrypted.getBytes(StandardCharsets.UTF_8));

        // Concatenate salt, iv, and cipherText
        byte[] combined = new byte[8 + 16 + cipherText.length];
        System.arraycopy(salt, 0, combined, 0, 8);
        System.arraycopy(iv, 0, combined, 8, 16);
        System.arraycopy(cipherText, 0, combined, 24, cipherText.length);

        // Base64 encode the combined array
        String encryptedData = Base64.getEncoder().encodeToString(combined);

        System.out.println("Encrypted Data: " + encryptedData);
    }
}