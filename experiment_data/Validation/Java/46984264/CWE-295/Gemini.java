import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CrossPlatformAES {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256; // Bits

    public static void main(String[] args) throws Exception {

        // 1. Generate a key (This should only be done once and the key stored securely)
        Key key = generateKey();

        // Sample data
        String data = "This is sensitive data.";

        // 2. Encrypt in Java (compatible with SQL Server)
        String encryptedData = encrypt(data, key);
        System.out.println("Encrypted (Base64): " + encryptedData);



        // 3. Decrypt in Java (demonstrating cross-platform compatibility)
        String decryptedData = decrypt(encryptedData, key);
        System.out.println("Decrypted: " + decryptedData);



        // For SQL Server decryption (example using T-SQL, adapt as needed)
        // You'll need to provide the key and IV as hex strings or VARBINARY literals.
        System.out.println("\n---SQL Server Decryption Example (T-SQL)---");

        String keyHex = bytesToHex(key.getEncoded());
        String ivHex = bytesToHex(getIV(encryptedData)); // Extract IV from encrypted data

        System.out.println("DECLARE @key VARBINARY(MAX) = 0x" + keyHex + ";");
        System.out.println("DECLARE @iv VARBINARY(16) = 0x" + ivHex + ";");
        System.out.println("DECLARE @encryptedData VARBINARY(MAX) = 0x" + getCiphertextHex(encryptedData) + ";");
        System.out.println("OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;");
        System.out.println("SELECT CAST(DECRYPTBYKEY(@encryptedData) AS VARCHAR(MAX));");
        System.out.println("CLOSE SYMMETRIC KEY SymmetricKey1;");

    }



    public static Key generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }


    public static String encrypt(String data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Generate a random IV (Initialization Vector) - CRUCIAL for security
        byte[] iv = new byte[16]; // 16 bytes for AES CBC
        new java.security.SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);


        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());


        // Prepend IV to the ciphertext (Essential for decryption)
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }



    public static String decrypt(String encryptedData, Key key) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedData);

        // Extract IV
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract ciphertext
        byte[] ciphertext = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(ciphertext);
        return new String(decryptedBytes);
    }


    // Helper functions for SQL Server interop
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static String getCiphertextHex(String encryptedBase64) {
      byte[] combined = Base64.getDecoder().decode(encryptedBase64);
      byte[] ciphertext = new byte[combined.length - 16]; // Remove IV
      System.arraycopy(combined, 16, ciphertext, 0, ciphertext.length);
      return  bytesToHex(ciphertext);
    }

    private static byte[] getIV(String encryptedBase64) {
        byte[] combined = Base64.getDecoder().decode(encryptedBase64);
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        return iv;
    }
}