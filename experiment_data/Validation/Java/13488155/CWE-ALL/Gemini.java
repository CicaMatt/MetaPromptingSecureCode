import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;

public class SecureHTMLFileEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int GCM_NONCE_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 16;  // 128 bits
    private static final String CONFIG_FILE = "config.properties";
    private static final String PASSWORD_PROPERTY = "encryption.password";

    public static void main(String[] args) throws Exception {
        // Load password from configuration file
        char[] password = loadPasswordFromConfig(); 
        if (password == null) {
            System.err.println("Error loading password from configuration.");
            return;
        }

        Path htmlFolder = Paths.get("html_files"); // Path to your HTML files
        Path encryptedFolder = Paths.get("encrypted_html"); // Path for encrypted files

        encryptFiles(htmlFolder, encryptedFolder, password);

        // Zero out the password array after use
        java.util.Arrays.fill(password, ' ');

        // Example of decrypting a file:
        decryptFile(encryptedFolder.resolve("index.html.enc"), htmlFolder.resolve("index.html.dec"), password);
        java.util.Arrays.fill(password, ' ');
    }

    private static char[] loadPasswordFromConfig() throws IOException {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            System.err.println("Configuration file not found. Create 'config.properties' with 'encryption.password' property.");
            return null;
        }

        Properties props = new Properties();
        try (InputStream input = new FileInputStream(configFile)) {
            props.load(input);
        }
        String passwordString = props.getProperty(PASSWORD_PROPERTY);
        if (passwordString == null || passwordString.isEmpty()) {
            System.err.println("Password property not found in configuration file.");
            return null;
        }
        return passwordString.toCharArray();
    }

    // ... (rest of the code from the previous improved response, including encryptFiles and decryptFile methods)
}

// ... (Include the encryptFiles and decryptFile functions exactly as they were in the improved response). 