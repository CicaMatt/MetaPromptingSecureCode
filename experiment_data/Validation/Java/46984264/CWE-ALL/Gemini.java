import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import java.security.SecureRandom;

public class CrossDatabaseEncryption {

    private static final String ALGORITHM = "AES/GCM/NoPadding";

    public static String encrypt(String data, String encryptionKey) throws Exception {
        byte[] salt = generateSalt();
        Key key = generateKey(encryptionKey, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        javax.crypto.spec.GCMParameterSpec parameterSpec = new javax.crypto.spec.GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());

        String base64Encrypted = Base64.getEncoder().encodeToString(encryptedBytes);
        String base64Salt = Base64.getEncoder().encodeToString(salt);
        String base64IV = Base64.getEncoder().encodeToString(iv);
        return base64Salt + ":" + base64IV + ":" + base64Encrypted;
    }

    public static String decrypt(String encryptedData, String encryptionKey) throws Exception {
        String[] parts = encryptedData.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid encrypted data format.");
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[2]);

        Key key = generateKey(encryptionKey, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        javax.crypto.spec.GCMParameterSpec parameterSpec = new javax.crypto.spec.GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static Key generateKey(String encryptionKey, byte[] salt) throws NoSuchAlgorithmException {
        int iterations = 10000;
        int keyLength = 256;

        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(encryptionKey.toCharArray(), salt, iterations, keyLength);
        javax.crypto.SecretKeyFactory keyFactory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = keyFactory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void main(String[] args) throws Exception {
        String encryptionKey = System.getenv("ENCRYPTION_KEY");
        String data = "Sensitive Data";
        String encrypted = encrypt(data, encryptionKey);
        System.out.println("Encrypted: " + encrypted);
        String decrypted = decrypt(encrypted, encryptionKey);
        System.out.println("Decrypted: " + decrypted);
    }
}
