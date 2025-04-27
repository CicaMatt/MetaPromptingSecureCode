import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;
import java.util.Base64;

public class SecureEncryptionExample {

    public static void main(String[] args) throws Exception {
        String password = "testpass"; // This should be securely stored and retrieved
        String toBeEncrypted = "AMOUNT=10&TID=#19:23&CURRENCY=EUR&LANGUAGE=DE&SUCCESS_URL=http://some.url/sucess&ERROR_URL=http://some.url/error&CONFIRMATION_URL=http://some.url/confirm&NAME=customer full name";
        
        // 8 byte salt, which can be random for each encryption
        byte[] salt = new byte[]{(byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF, (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78};
        
        // PBKDF2 key derivation
        int iterations = 10000; // Increased for better security
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = factory.generateSecret(spec).getEncoded();
        
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        
        // AES CBC encryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        
        byte[] cipherText = cipher.doFinal(toBeEncrypted.getBytes(StandardCharsets.UTF_8));
        
        // Concatenate salt, iv, and ciphertext in OpenSSL-compatible format
        byte[] opensslOutput = concat(concat("Salted__".getBytes(StandardCharsets.UTF_8), salt), concat(iv, cipherText));
        
        // Encode the result in Base64
        String encryptedOutput = Base64.getEncoder().encodeToString(opensslOutput);
        
        System.out.println("Encrypted Output: " + encryptedOutput);
    }

    private static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}