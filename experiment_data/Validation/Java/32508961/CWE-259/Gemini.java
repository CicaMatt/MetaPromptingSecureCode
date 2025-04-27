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

    private static final String PASSWORD_ENV_VAR = "EXTERNAL_SYSTEM_PASSWORD"; // Store password in environment variable
    private static final String SALT_ENV_VAR = "EXTERNAL_SYSTEM_SALT"; // Store salt in environment variable
    private static final int ITERATIONS = 10000; // High iteration count for PBKDF2
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String encrypt(String data) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        String password = System.getenv(PASSWORD_ENV_VAR); // Retrieve password from environment variable
        String salt = System.getenv(SALT_ENV_VAR);

        if (password == null || password.isEmpty() || salt == null || salt.isEmpty()) {
            throw new IllegalArgumentException("Password and Salt environment variables must be set.");
        }

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), ITERATIONS, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Encrypt the message
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
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

        String data = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";

        // Set example environment variables (In real application, manage these securely)
        //  System.setProperty(PASSWORD_ENV_VAR, "testpass");  // Use a stronger password!!
        // System.setProperty(SALT_ENV_VAR, "15charRandomSalt");  // Use a securely generated salt!!

        String encryptedData = encrypt(data);
        System.out.println(encryptedData);

    }
}