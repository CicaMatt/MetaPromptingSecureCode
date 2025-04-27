import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESCrypto {

    private static final String AES = "AES";
    private static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    private static final int GCM_NONCE_LENGTH = 12; // 12 bytes for GCM recommended
    private static final int GCM_TAG_LENGTH = 128; 
    private static final int AES_KEY_SIZE = 256; // 256-bit key

    // Generate a new AES key (for demonstration purposes)
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(AES_KEY_SIZE);
        return keyGen.generateKey();
    }

    // Encrypt a message using AES-256 GCM
    public static String encrypt(String plainText, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        // Concatenate IV and CipherText for easy transport
        byte[] encoded = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encoded, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encoded, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(encoded);
    }

    // Decrypt a message using AES-256 GCM
    public static String decrypt(String cipherText, SecretKey key) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);

        // Extract IV which is prepended to encrypted message
        byte[] iv = new byte[GCM_NONCE_LENGTH];
        System.arraycopy(decodedBytes, 0, iv, 0, iv.length);

        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        
        // Extract the encrypted part
        byte[] encryptedBytes = new byte[decodedBytes.length - GCM_NONCE_LENGTH];
        System.arraycopy(decodedBytes, GCM_NONCE_LENGTH, encryptedBytes, 0, encryptedBytes.length);

        return new String(cipher.doFinal(encryptedBytes));
    }

    public static void main(String[] args) {
        try {
            SecretKey key = generateKey(); // In practice, securely store and load key
            byte[] iv = new byte[GCM_NONCE_LENGTH]; // Generate IV appropriately for each encryption
            // For demonstration purposes, IV is all-zero; NEVER use the same IV for different messages

            String original = "Sensitive Data";
            String encrypted = encrypt(original, key, iv);
            String decrypted = decrypt(encrypted, key);

            System.out.println("Original: " + original);
            System.out.println("Encrypted: " + encrypted);
            System.out.println("Decrypted: " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}