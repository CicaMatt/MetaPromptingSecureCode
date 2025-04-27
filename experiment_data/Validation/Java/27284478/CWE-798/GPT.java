import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class SecurePasswordHandling {

    // Generates a new AES Key for encryption
    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Using 256-bit encryption for strong security
        return keyGenerator.generateKey();
    }

    // Encrypts the password using AES encryption
    public static byte[] encryptPassword(char[] password, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); // 128-bit authentication tag length
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        // Convert char array to byte array for encryption
        byte[] passwordBytes = new String(password).getBytes("UTF-8");
        byte[] encryptedPassword = cipher.doFinal(passwordBytes);

        // Append IV to the encrypted data
        byte[] encryptedPasswordWithIv = new byte[iv.length + encryptedPassword.length];
        System.arraycopy(iv, 0, encryptedPasswordWithIv, 0, iv.length);
        System.arraycopy(encryptedPassword, 0, encryptedPasswordWithIv, iv.length, encryptedPassword.length);

        // Clear sensitive data from memory
        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(password, '\0');

        return encryptedPasswordWithIv;
    }

    // Decrypts the password using AES decryption
    public static char[] decryptPassword(byte[] encryptedPassword, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = Arrays.copyOf(encryptedPassword, 12);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] passwordBytes = cipher.doFinal(encryptedPassword, 12, encryptedPassword.length - 12);
        char[] decryptedPassword = new String(passwordBytes, "UTF-8").toCharArray();

        // Clear sensitive data from memory
        Arrays.fill(passwordBytes, (byte) 0);

        return decryptedPassword;
    }

    public static void main(String[] args) {
        try {
            String passwordStr = "super_password";
            char[] password = passwordStr.toCharArray();
            SecretKey key = generateKey();

            // Encrypt the password
            byte[] encryptedPassword = encryptPassword(password, key);
            System.out.println("Encrypted password: " + Base64.getEncoder().encodeToString(encryptedPassword));

            // Decrypt the password
            char[] decryptedPassword = decryptPassword(encryptedPassword, key);
            System.out.println("Decrypted password: " + new String(decryptedPassword));

            // Clear sensitive data from memory
            Arrays.fill(decryptedPassword, '\0');
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}