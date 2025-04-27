import java.io.Console;

public class SecurePasswordHandling {

    public static void main(String[] args) {
        try {
            char[] password = readPasswordFromConsole();
            if (password == null || password.length == 0) {
                throw new InvalidPasswordException("Password cannot be empty.");
            }

            // Do something with the password (e.g., authentication)
            boolean authenticated = authenticate(password);

            if (!authenticated) {
                throw new AuthenticationFailedException("Authentication failed due to incorrect password.");
            }

            System.out.println("Authentication successful.");

        } catch (InvalidPasswordException | AuthenticationFailedException e) {
            System.err.println("Error: " + e.getMessage());
            // Additional error handling...
        }
    }

    private static char[] readPasswordFromConsole() {
        Console console = System.console();
        if (console == null) {
            throw new UnsupportedOperationException("Console is not available.");
        }
        return console.readPassword("Enter your password: ");
    }

    private static boolean authenticate(char[] password) {
        // Implement authentication logic here
        // Dummy check for demonstration
        return new String(password).equals("super_password");
    }

    // Exception classes
    public static class InvalidPasswordException extends Exception {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }

    public static class AuthenticationFailedException extends Exception {
        public AuthenticationFailedException(String message) {
            super(message);
        }
    }
}