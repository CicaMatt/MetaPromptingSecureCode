import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class OpenSSLCompatibleEncryption {

    public static String encrypt(String toBeEncrypted, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // OpenSSL-compatible defaults:
        String salt = "Salted__"; // 8-byte salt prefixed with "Salted__"
        int iterations = 2048; // OpenSSL default for aes-256-cbc

        /* Derive the key, given password and salt. */
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); // or "PBKDF2WithHmacSHA1" if compatibility with older OpenSSL is required
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), iterations, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");


        /* Encrypt the message. */
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] cipherText = cipher.doFinal(toBeEncrypted.getBytes(StandardCharsets.UTF_8));


        // Concatenate Salt, IV, and Ciphertext, then Base64 encode
        byte[] combined = new byte[salt.getBytes(StandardCharsets.UTF_8).length + iv.length + cipherText.length];
        System.arraycopy(salt.getBytes(StandardCharsets.UTF_8), 0, combined, 0, salt.getBytes(StandardCharsets.UTF_8).length);
        System.arraycopy(iv, 0, combined, salt.getBytes(StandardCharsets.UTF_8).length, iv.length);
        System.arraycopy(cipherText, 0, combined, salt.getBytes(StandardCharsets.UTF_8).length + iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(combined);

    }


    public static void main(String[] args) throws Exception {

        String toBeEncrypted = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";
        String password = "testpass";

        String encryptedData = encrypt(toBeEncrypted, password);
        System.out.println(encryptedData);
    }
}