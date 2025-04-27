import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CrossCompatibleEncryption {

    // Use a standard algorithm available in both MySQL and SQL Server
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding"; 
    private static final String KEY = "Your16ByteKey123"; // Must be 16 bytes for AES-128

    public static String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // Encode for safe storage
    }

    public static String decrypt(String encryptedData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    public static void main(String[] args) {
        String data = "Sensitive Data";

        try {
            String encrypted = encrypt(data);
            System.out.println("Encrypted: " + encrypted);

            String decrypted = decrypt(encrypted);
            System.out.println("Decrypted: " + decrypted);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm not found: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.err.println("Padding not found: " + e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println("Invalid key: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.err.println("Illegal block size: " + e.getMessage());
        } catch (BadPaddingException e) {
            System.err.println("Bad padding: " + e.getMessage());
        }
    }
}