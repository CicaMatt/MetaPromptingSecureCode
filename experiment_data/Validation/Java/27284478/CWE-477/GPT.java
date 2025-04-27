import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
import java.util.Arrays;

public class SecurePasswordHandling {
    // Securely hash a password using PBKDF2
    public static String hashPassword(String password, byte[] salt) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 65536;
        int keyLength = 256;
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    // Generate a new salt
    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static void main(String[] args) {
        String password = "super_password"; // Obtain password from input securely
        try {
            byte[] salt = getSalt();
            String hashedPassword = hashPassword(password, salt);

            System.out.println("Salt: " + Base64.getEncoder().encodeToString(salt));
            System.out.println("Hashed Password: " + hashedPassword);
            // Ensure the password is erased as soon as it's no longer needed
            Arrays.fill(password.toCharArray(), '\u0000');

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}