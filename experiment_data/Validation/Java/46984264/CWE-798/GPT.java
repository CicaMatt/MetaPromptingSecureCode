import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;

public class EncryptionMigration {

    private static final String ALGORITHM_AES = "AES";
    
    public static void main(String[] args) throws Exception {
        // Sample encrypted data to decrypt and re-encrypt
        String encryptedData = "yourEncryptedDataFromMySQL";
        String key = "yourAES256Key"; // 256-bit key for AES-256 decryption

        // Decrypt the data using AES-256
        byte[] decryptedData = decrypt(encryptedData, key);

        // Re-encrypt the data using AES-128
        SecretKey aes128Key = generateAESKey(128); // Generate AES 128-bit key
        byte[] reEncryptedData = encryptWithAES128(decryptedData, aes128Key);
        
        // Output re-encrypted data for SQL Server compatibility
        System.out.println("Re-encrypted Data (Base64): " + DatatypeConverter.printBase64Binary(reEncryptedData));
    }

    // Decrypts data using AES-256
    public static byte[] decrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
        SecretKeySpec keySpec = new SecretKeySpec(deriveKey(key, 32), ALGORITHM_AES); // 32 bytes for 256-bit
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(DatatypeConverter.parseBase64Binary(data));
    }

    // Encrypt the data using AES-128
    public static byte[] encryptWithAES128(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // Generate AES Secret Key
    public static SecretKey generateAESKey(int keySize) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM_AES);
        keyGen.init(keySize, new SecureRandom());
        return keyGen.generateKey();
    }
    
    // Simulate key derivation for AES-256
    public static byte[] deriveKey(String key, int length) {
        byte[] keyBytes = key.getBytes();
        byte[] derivedKey = new byte[length];
        System.arraycopy(keyBytes, 0, derivedKey, 0, Math.min(keyBytes.length, derivedKey.length));
        return derivedKey;
    }
}