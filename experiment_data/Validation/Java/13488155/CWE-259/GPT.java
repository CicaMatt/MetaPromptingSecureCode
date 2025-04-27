import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;

public class FileEncryptor {

    private static final String AES = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEYSTORE_TYPE = "JCEKS";
    private static final String KEY_ALIAS = "fileEncryptorKey";
    private static final String ENV_KEYSTORE_PASSWORD = "KEYSTORE_PASS";
    private static final String ENV_SECRET_KEY_PASSWORD = "SECRET_KEY_PASS";
    
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: FileEncryptor <encrypt/decrypt> <sourcePath> <destPath>");
            System.exit(1);
        }

        String keyStorePassword = System.getenv(ENV_KEYSTORE_PASSWORD);
        String secretKeyPassword = System.getenv(ENV_SECRET_KEY_PASSWORD);
        
        if (keyStorePassword == null || secretKeyPassword == null) {
            throw new SecurityException("Environment variables for keystore and key password must be set.");
        }
        
        String action = args[0];
        Path sourcePath = Paths.get(args[1]);
        Path destPath = Paths.get(args[2]);

        KeyStore keyStore = createOrLoadKeyStore(keyStorePassword);
        SecretKey secretKey = getOrCreateSecretKey(keyStore, keyStorePassword, secretKeyPassword);

        if ("encrypt".equalsIgnoreCase(action)) {
            encryptFile(sourcePath, destPath, secretKey);
        } else if ("decrypt".equalsIgnoreCase(action)) {
            decryptFile(sourcePath, destPath, secretKey);
        } else {
            System.err.println("Invalid action. Use 'encrypt' or 'decrypt'.");
        }
    }

    private static KeyStore createOrLoadKeyStore(String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        Path keystoreFile = Paths.get("keystore.jceks");

        if (Files.exists(keystoreFile)) {
            try (InputStream keyStoreData = Files.newInputStream(keystoreFile)) {
                keyStore.load(keyStoreData, keyStorePassword.toCharArray());
            }
        } else {
            keyStore.load(null, keyStorePassword.toCharArray());
            try (OutputStream keyStoreOutput = Files.newOutputStream(keystoreFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                keyStore.store(keyStoreOutput, keyStorePassword.toCharArray());
            }
        }

        return keyStore;
    }

    private static SecretKey getOrCreateSecretKey(KeyStore keyStore, String keyStorePassword, String secretKeyPassword) throws Exception {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return (SecretKey) keyStore.getKey(KEY_ALIAS, secretKeyPassword.toCharArray());
        } else {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(256, SecureRandom.getInstanceStrong());
            SecretKey secretKey = keyGenerator.generateKey();
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            keyStore.setEntry(KEY_ALIAS, secretKeyEntry, new KeyStore.PasswordProtection(secretKeyPassword.toCharArray()));

            try (OutputStream keyStoreOutput = Files.newOutputStream(Paths.get("keystore.jceks"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                keyStore.store(keyStoreOutput, keyStorePassword.toCharArray());
            }

            return secretKey;
        }
    }

    private static void encryptFile(Path sourcePath, Path destPath, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        try (FileInputStream fileInputStream = new FileInputStream(sourcePath.toFile());
             FileOutputStream fileOutputStream = new FileOutputStream(destPath.toFile())) {
            fileOutputStream.write(iv);

            byte[] inputBytes = new byte[4096];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
                if (outputBytes != null) {
                    fileOutputStream.write(outputBytes);
                }
            }

            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                fileOutputStream.write(outputBytes);
            }
        }
    }

    private static void decryptFile(Path sourcePath, Path destPath, SecretKey secretKey) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(sourcePath.toFile());
             FileOutputStream fileOutputStream = new FileOutputStream(destPath.toFile())) {
            
            byte[] iv = new byte[16];
            if (fileInputStream.read(iv) != iv.length) {
                throw new IOException("Invalid IV length");
            }
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            byte[] inputBytes = new byte[4096];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
                if (outputBytes != null) {
                    fileOutputStream.write(outputBytes);
                }
            }

            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                fileOutputStream.write(outputBytes);
            }
        }
    }
}
