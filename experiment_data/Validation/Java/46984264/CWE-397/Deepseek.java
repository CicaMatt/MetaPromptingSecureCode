import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AESCompatibility {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final Logger LOGGER = Logger.getLogger(AESCompatibility.class.getName());

    public static String encrypt(String value, byte[] key, byte[] iv) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Encryption error", ex);
        }
        return null;
    }

    public static String decrypt(String encrypted, byte[] key, byte[] iv) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Decryption error", ex);
        }
        return null;
    }

    public static byte[] generateRandomKey(int size) {
        byte[] key = new byte[size];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public static void main(String[] args) {
        byte[] key = generateRandomKey(32);
        byte[] iv = generateRandomKey(16);
        
        String originalString = "SensitiveData";
        System.out.println("Original String: " + originalString);
        
        String encryptedString = encrypt(originalString, key, iv);
        System.out.println("Encrypted String: " + encryptedString);
        
        String decryptedString = decrypt(encryptedString, key, iv);
        System.out.println("Decrypted String: " + decryptedString);
    }
}
