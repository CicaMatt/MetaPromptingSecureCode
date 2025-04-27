import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class CrossPlatformEncryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // Use a mode compatible with both MySQL and SQL Server


    public static void main(String[] args) throws Exception {

        // Fetch the encryption key from a secure location. DO NOT HARDCODE!
        // Examples: Environment variables, secure configuration file, secrets management service.
        String encodedKey = System.getenv("ENCRYPTION_KEY"); // Example using environment variable
        if (encodedKey == null || encodedKey.isEmpty()) {
            throw new IllegalStateException("Encryption key not found in environment variable 'ENCRYPTION_KEY'");
        }

        byte[] keyBytes = Base64.getDecoder().decode(encodedKey); // Decode the Base64 encoded key

        Key key = new SecretKeySpec(keyBytes, ALGORITHM);


        String dataToEncrypt = "Sensitive Data";


        // Encrypt in Java
        String encryptedData = encrypt(dataToEncrypt, key);
        System.out.println("Encrypted Data (Base64 encoded): " + encryptedData);

        // Decrypt in Java (for demonstration)
        String decryptedData = decrypt(encryptedData, key);
        System.out.println("Decrypted Data: " + decryptedData);


        // SQL Server Example (Illustrative - adapt as needed for your SQL Server environment)
        System.out.println("Example SQL Server Decryption (Adapt for your SQL Server environment):");
        System.out.println("DECLARE @encryptedData varbinary(max) = '" + bytesToHex(Base64.getDecoder().decode(encryptedData)) + "';");
        System.out.println("OPEN SYMMETRIC KEY MySymmetricKey DECRYPTION BY CERTIFICATE MyCertificate;");
        System.out.println("SELECT CONVERT(varchar, DecryptByKey(@encryptedData));"); // Demonstrates using a key defined in SQL Server. Adjust as needed
        System.out.println("CLOSE SYMMETRIC KEY MySymmetricKey;"); //Remember to close symmetric key after decryption to prevent potential security issues. 
        

        //MySQL Example decryption (Illustrative. Must adapt to your env):
        System.out.println("\nExample MySQL Decryption (Adapt for your MySQL environment):");
        System.out.println("SET @encrypted_data = '" + encryptedData + "';");
        System.out.println("SET @key = '" + encodedKey + "';");  // Replace with your method of retrieving the key
        System.out.println("SELECT AES_DECRYPT(FROM_BASE64(@encrypted_data), @key);");
    }


    public static String encrypt(String data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes); // Encode to Base64 for easy transport and storage
    }



    public static String decrypt(String encryptedData, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }



    // Utility for converting byte array to hexadecimal string for SQL Server example
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}