import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class OpenSSLCompatibleEncryption {

    public static void main(String[] args) throws Exception {
        String password = "testpass";
        String plaintext = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/success&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        // Encrypt the message
        String encrypted = encryptWithOpenSSLFormat(password, plaintext);
        System.out.println("Encrypted String: " + encrypted);
    }

    public static String encryptWithOpenSSLFormat(String password, String plaintext) throws Exception {
        // Generate a random salt
        byte[] salt = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        // Derive key and IV
        byte[] keyAndIV = generateKeyAndIV(password, salt);
        byte[] key = Arrays.copyOfRange(keyAndIV, 0, 32);
        byte[] iv = Arrays.copyOfRange(keyAndIV, 32, 48);

        // Initialize cipher
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        // Encrypt data
        byte[] encryptedData = cipher.doFinal(plaintext.getBytes("UTF-8"));

        // Prepend the "Salted__" prefix and the salt
        byte[] opensslEncrypted = new byte[16 + encryptedData.length];
        System.arraycopy("Salted__".getBytes("UTF-8"), 0, opensslEncrypted, 0, 8);
        System.arraycopy(salt, 0, opensslEncrypted, 8, 8);
        System.arraycopy(encryptedData, 0, opensslEncrypted, 16, encryptedData.length);

        // Base64 encode the final output
        return Base64.getEncoder().encodeToString(opensslEncrypted);
    }

    private static byte[] generateKeyAndIV(String password, byte[] salt) throws Exception {
        final byte[] passwordBytes = password.getBytes("UTF-8");
        final MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyAndIV = new byte[48];
        byte[] previous = null;
        int remainingBytes = keyAndIV.length;
        int index = 0;

        while (remainingBytes > 0) {
            md.reset();

            // Concatenate previous, password and salt
            if (previous != null) {
                md.update(previous);
            }
            md.update(passwordBytes);
            md.update(salt, 0, 8); // Use the first 8 bytes of salt

            // Hash iteration
            previous = md.digest();

            int toCopy = Math.min(previous.length, remainingBytes);
            System.arraycopy(previous, 0, keyAndIV, index, toCopy);

            index += toCopy;
            remainingBytes -= toCopy;
        }
        return keyAndIV;
    }
}