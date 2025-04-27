import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;

public class FileEncryptorDecryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void main(String[] args) {
        try {
            // Generate a secret key for encryption and decryption
            Key secretKey = generateKey();

            // Path to the folder containing HTML files
            String inputFolderPath = "path/to/html/folder";
            String encryptedFolderPath = "path/to/encrypted/folder";
            String decryptedFolderPath = "path/to/decrypted/folder";

            // Encrypt the entire folder
            encryptFolder(inputFolderPath, encryptedFolderPath, secretKey);

            // Decrypt the entire folder
            decryptFolder(encryptedFolderPath, decryptedFolderPath, secretKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Key generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // 128-bit key size
        return keyGen.generateKey();
    }

    public static void encryptFolder(String inputFolderPath, String encryptedFolderPath, Key secretKey) throws Exception {
        File inputFolder = new File(inputFolderPath);
        File[] files = inputFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    encryptFile(file.getAbsolutePath(), encryptedFolderPath + "/" + file.getName(), secretKey);
                }
            }
        }
    }

    public static void decryptFolder(String encryptedFolderPath, String decryptedFolderPath, Key secretKey) throws Exception {
        File encryptedFolder = new File(encryptedFolderPath);
        File[] files = encryptedFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    decryptFile(file.getAbsolutePath(), decryptedFolderPath + "/" + file.getName(), secretKey);
                }
            }
        }
    }

    public static void encryptFile(String inputFilePath, String outputFilePath, Key secretKey) throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, secretKey, inputFilePath, outputFilePath);
    }

    public static void decryptFile(String inputFilePath, String outputFilePath, Key secretKey) throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, secretKey, inputFilePath, outputFilePath);
    }

    private static void doCrypto(int cipherMode, Key secretKey, String inputFilePath, String outputFilePath) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(cipherMode, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFilePath);
        byte[] inputBytes = new byte[(int) new File(inputFilePath).length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFilePath);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();
    }
}