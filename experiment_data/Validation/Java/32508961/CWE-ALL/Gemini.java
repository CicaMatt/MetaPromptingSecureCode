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

public class SecureEncryption {

    private static final int ITERATIONS = 65536; // Increased iterations for stronger key derivation
    private static final int KEY_LENGTH = 256; // Key length in bits
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String encrypt(String data, String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Encrypt the data
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));


        // Concatenate IV and ciphertext, then Base64 encode
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
        return Base64.getEncoder().encodeToString(combined);

    }



    public static void main(String[] args) throws Exception {
        // In real application, retrieve password and salt from secure configuration. DO NOT hardcode
        //  For demonstration purpose only, sensitive data is hardcoded.

        String password =  System.getenv("ENCRYPTION_PASSWORD"); // Retrieve from environment variable
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Encryption password not set in environment variable ENCRYPTION_PASSWORD");
        }
        String salt = System.getenv("ENCRYPTION_SALT"); // Retrieve from environment variable. Ensure it's generated with SecureRandom.
        if (salt == null || salt.isEmpty()) {
            throw new IllegalArgumentException("Encryption salt not set in environment variable ENCRYPTION_SALT");
        }


        String data = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        String encryptedData = encrypt(data, password, salt);
        System.out.println(encryptedData);


    }
}