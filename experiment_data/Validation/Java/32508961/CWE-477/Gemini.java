import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class OpenSSLCompatibleEncryption {

    public static String encrypt(String plaintext, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // OpenSSL uses a fixed salt for -aes-256-cbc -a (Salted__ prefix)
        byte[] salt = "Salted__".getBytes(StandardCharsets.UTF_8);

        // OpenSSL derives a 256-bit key from the password using MD5
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] key = md5.digest(password.getBytes(StandardCharsets.UTF_8));
        key = combine(key, md5.digest(combine(salt, password.getBytes(StandardCharsets.UTF_8))));
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        // Encrypt using AES-256-CBC with PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();


        // Concatenate salt, iv, and ciphertext, then base64 encode
        byte[] combined = combine(combine(salt, iv), ciphertext);
        return Base64.getEncoder().encodeToString(combined);

    }


    private static byte[] combine(byte[] a, byte[] b) {
        byte[] combined = new byte[a.length + b.length];
        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);
        return combined;
    }



    public static void main(String[] args) throws Exception {
        String plaintext = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";
        String password = "testpass";

        String encrypted = encrypt(plaintext, password);
        System.out.println(encrypted);

    }
}