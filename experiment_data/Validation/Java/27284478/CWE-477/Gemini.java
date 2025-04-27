import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.Cipher;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.BadPaddingException;


public class SecurePasswordHandling {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"; // Strong cipher
    private static final int ITERATIONS = 10000; // Higher iterations for stronger security
    private static final int KEY_LENGTH = 256; // Key length in bits
    private static final int SALT_LENGTH = 16; // Salt length in bytes
    private static final int IV_LENGTH = 16; // Initialization vector length in bytes


    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey key = skf.generateSecret(spec);
        byte[] hash = key.getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String encryptPassword(String password, SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new javax.crypto.spec.IvParameterSpec(iv));
        byte[] encryptedPassword = cipher.doFinal(password.getBytes());

        return Base64.getEncoder().encodeToString(encryptedPassword);
    }


    public static String decryptPassword(String encryptedPassword, SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new javax.crypto.spec.IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));

        return new String(decryptedBytes);

    }


    public static void main(String[] args) throws Exception {
         // Generate salt (do this once and store securely with the hashed password)
        byte[] salt = new byte[SALT_LENGTH];
        new java.security.SecureRandom().nextBytes(salt);

        // Generate IV (do this for every encryption and store with ciphertext)
        byte[] iv = new byte[IV_LENGTH];
        new java.security.SecureRandom().nextBytes(iv);

        String password = "super_password";
        String hashedPassword = hashPassword(password, salt);
        System.out.println("Hashed password: " + hashedPassword);


        // For encryption/decryption (demonstration - In real applications, retrieve the hashed password and salt from storage)
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH); // Ideally get password from user input
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey keyForEncryption = skf.generateSecret(spec);

        String encryptedPassword = encryptPassword(password, keyForEncryption, iv);
        System.out.println("Encrypted Password: " + encryptedPassword);


        String decryptedPassword = decryptPassword(encryptedPassword, keyForEncryption, iv);
        System.out.println("Decrypted Password: " + decryptedPassword);

    }
}