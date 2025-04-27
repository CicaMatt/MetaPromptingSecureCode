import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureEncryption {

    public static String encrypt(String plaintext, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // Use a strong, vetted algorithm: AES-256 in CBC mode with PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Generate a secure random IV (Initialization Vector) - crucial for CBC mode
        byte[] iv = new byte[cipher.getBlockSize()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Key derivation using PBKDF2WithHmacSHA256 (industry-standard key derivation function)
        // Use a high iteration count for stronger security
        int iterations = 10000; // Recommended: At least 10,000 iterations
        int keyLength = 256; // For AES-256
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(factory.generateSecret(new javax.crypto.spec.PBEKeySpec(password.toCharArray(), iv, iterations, keyLength)).getEncoded(), "AES");


        // Initialize the cipher in encryption mode
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        // Encrypt the data
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));


        // Concatenate IV and ciphertext for decryption later and then encode as Base64
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(combined);
    }



    public static void main(String[] args) throws Exception {

        String plaintext = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";
        String password = "testpass";

        String encryptedData = encrypt(plaintext, password);
        System.out.println(encryptedData);



    }
}