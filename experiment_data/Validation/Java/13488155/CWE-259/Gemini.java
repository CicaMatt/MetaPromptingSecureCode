import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class SecureHTMLAccess {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String PASSWORD_ENV_VAR = "HTML_ENCRYPTION_PASSWORD";

    public static void main(String[] args) throws Exception {
        String htmlFolderPath = "html_files";
        String encryptedFolderPath = "encrypted_html";

        String password = System.getenv(PASSWORD_ENV_VAR);
        if (password == null || password.isEmpty()) {
            throw new SecurityException("Encryption password not set in environment variable: " + PASSWORD_ENV_VAR);
        }

        encryptDecryptDirectory(htmlFolderPath, encryptedFolderPath, password, true);
    }

    private static void encryptDecryptDirectory(String inputDir, String outputDir, String password, boolean encrypt) throws Exception {
        File inputDirectory = new File(inputDir);
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        for (File file : inputDirectory.listFiles()) {
            if (file.isFile()) {
                String inputFilePath = file.getAbsolutePath();
                String outputFilePath = outputDir + "/" + file.getName();
                if (encrypt) {
                    encryptFile(inputFilePath, outputFilePath, password);
                } else {
                    decryptFile(inputFilePath, outputFilePath, password);
                }
            }
        }
    }

    private static void encryptFile(String inputFile, String outputFile, String password) throws Exception {
        byte[] salt = generateSalt();
        byte[] iv = generateIv();
        SecretKey key = generateKey(password, salt);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
            fos.write(salt);
            fos.write(iv);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    private static void decryptFile(String inputFile, String outputFile, String password) throws Exception {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] salt = new byte[16];
            byte[] iv = new byte[16];
            if (fis.read(salt) != 16 || fis.read(iv) != 16) {
                throw new IOException("Invalid file format");
            }
            SecretKey key = generateKey(password, salt);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            try (CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private static SecretKey generateKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100000, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] generateIv() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }
}
