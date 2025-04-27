import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class SecurePasswordHandling {
    
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256; // bits
    private static final int GCM_IV_LENGTH = 12; // bytes
    private static final int GCM_TAG_LENGTH = 16; // bytes

    public static void main(String[] args) throws Exception {
        SecretKey key = generateKey();
        String password = "super_password";

        // Encrypt
        byte[] encryptedPassword = encryptPassword(password.toCharArray(), key);
        System.out.println("Encrypted Password: " + Base64.getEncoder().encodeToString(encryptedPassword));

        // Decrypt
        String decryptedPassword = decryptPassword(encryptedPassword, key);
        System.out.println("Decrypted Password: " + decryptedPassword);
        
        // Clear sensitive data from memory
        password = null; // Clear the string
    }

    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE, new SecureRandom());
        return keyGen.generateKey();
    }

    private static byte[] encryptPassword(char[] password, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] passwordBytes = new String(password).getBytes(); // Converting char[] to byte[]
        byte[] encryptedPassword = cipher.doFinal(passwordBytes);

        // Prepend IV to the encrypted password for use in decryption
        byte[] result = new byte[iv.length + encryptedPassword.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedPassword, 0, result, iv.length, encryptedPassword.length);

        // Clear sensitive data
        for (int i = 0; i < password.length; i++) {
            password[i] = 0;
        }
        
        return result;
    }

    private static String decryptPassword(byte[] encryptedPassword, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

        // Extract the IV from the ciphertext
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] ciphertext = new byte[encryptedPassword.length - GCM_IV_LENGTH];
        System.arraycopy(encryptedPassword, 0, iv, 0, iv.length);
        System.arraycopy(encryptedPassword, iv.length, ciphertext, 0, ciphertext.length);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decryptedPasswordBytes = cipher.doFinal(ciphertext);
        return new String(decryptedPasswordBytes);
    }
}