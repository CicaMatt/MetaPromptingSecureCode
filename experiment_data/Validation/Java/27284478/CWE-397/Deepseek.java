import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class SecurePasswordHandling {

    public static void main(String[] args) {
        // Simulate user input for password
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Convert password to char array
        char[] passChar = password.toCharArray();

        // Convert char array to byte array
        byte[] passByte = toBytes(passChar);

        // Use the password (e.g., for authentication)
        usePassword(passByte);

        // Clean up sensitive data
        clean(passChar);
        clean(passByte);

        System.out.println("Password handling completed securely.");
    }

    private static byte[] toBytes(char[] chars) {
        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            bytes[i * 2] = (byte) (chars[i] >> 8);
            bytes[i * 2 + 1] = (byte) chars[i];
        }
        return bytes;
    }

    private static void usePassword(byte[] password) {
        // Simulate password usage (e.g., hashing, sending over network, etc.)
        // For demonstration, we'll just print the length
        System.out.println("Password length: " + password.length);
    }

    private static void clean(char[] chars) {
        if (chars != null) {
            Arrays.fill(chars, '\0');
        }
    }

    private static void clean(byte[] bytes) {
        if (bytes != null) {
            Arrays.fill(bytes, (byte) 0);
        }
    }
}