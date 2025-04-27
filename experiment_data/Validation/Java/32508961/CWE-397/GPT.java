import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionUtility {

    public static class EncryptionException extends Exception {
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static String encrypt(String toBeEncrypted, String password) throws EncryptionException {
        try {
            // Salt and iteration count
            byte[] salt = "15charRandomSalt".getBytes(StandardCharsets.UTF_8);
            int iterations = 100;

            // Key derivation
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Cipher initialization
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            
            // Retrieving IV
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

            // Encryption process
            byte[] cipherText = cipher.doFinal(toBeEncrypted.getBytes(StandardCharsets.UTF_8));

            // Concatenate salt, iv, and ciphertext and encode
            byte[] combined = new byte[salt.length + iv.length + cipherText.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(iv, 0, combined, salt.length, iv.length);
            System.arraycopy(cipherText, 0, combined, salt.length + iv.length, cipherText.length);

            // Encode everything in Base64
            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception ex) {
            // Throw custom exception with context
            throw new EncryptionException("Error during encryption process", ex);
        }
    }

    public static void main(String[] args) {
        try {
            String output = encrypt("Your data here", "passPhrase");
            System.out.println("Encrypted Data: " + output);
        } catch (EncryptionException e) {
            e.printStackTrace();
            // Consider logging the error for further analysis
        }
    }
}