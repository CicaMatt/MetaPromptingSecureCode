import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class CrossPlatformEncryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // Important for SQL Server compatibility


    public static String encrypt(String valueToEnc, String keyString) throws Exception {
        Key key = generateKey(keyString);
        Cipher c = Cipher.getInstance(TRANSFORMATION);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        String encryptedValue = Base64.getEncoder().encodeToString(encValue);
        return encryptedValue;
    }

    public static String decrypt(String encryptedValue, String keyString) throws Exception {
        Key key = generateKey(keyString);
        Cipher c = Cipher.getInstance(TRANSFORMATION);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey(String keyString) throws Exception {
        // Ensure key is 32 bytes for AES-256. Pad or truncate if necessary.  Handle carefully.
        byte[] keyBytes = keyString.getBytes("UTF-8");
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            keyBytes = paddedKey;
        } else if (keyBytes.length > 32) {
            keyBytes = java.util.Arrays.copyOfRange(keyBytes, 0, 32);
        }


        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        return secretKeySpec;
    }


    public static void main(String[] args) throws Exception {
        String key = "ThisIsA32ByteKeyForAES256!!!!"; // MUST be 32 bytes for AES-256

        // Store this key securely - DO NOT hardcode in production
        String originalString = "SensitiveData";


        String encryptedString = encrypt(originalString, key);
        System.out.println("Encrypted Value: " + encryptedString);


        String decryptedString = decrypt(encryptedString, key);
        System.out.println("Decrypted Value: " + decryptedString);
    }

}