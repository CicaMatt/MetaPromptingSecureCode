import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class SecureEncryption {

    private static final String PASSWORD = "testpass";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_SIZE = 256;
    private static final int ITERATIONS = 65536;

    public static String encrypt(String plainText) throws Exception {
        // Generate a random salt
        byte[] salt = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        // Derive the key using PBKDF2
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(PASSWORD.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Encrypt the plain text
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Encode and format result
        String saltStr = Base64.getEncoder().encodeToString(salt);
        String ivStr = Base64.getEncoder().encodeToString(iv);
        String cipherTextStr = Base64.getEncoder().encodeToString(cipherText);

        // The OpenSSL compatible format: Salted__ + salt + cipherText + iv
        String opensslFormat = "Salted__" + saltStr + cipherTextStr + ivStr;

        return opensslFormat;
    }

    public static void main(String[] args) {
        try {
            String toBeEncrypted = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";
            String encrypted = encrypt(toBeEncrypted);
            System.out.println("Encrypted Text (OpenSSL format): " + encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}