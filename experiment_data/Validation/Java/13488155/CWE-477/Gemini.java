import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class HTMLFileEncryptor {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; // Secure cipher
    private static final String KEY_STRING = "YourStrongPassword123"; //  MUST be changed - Use a STRONG, randomly generated key
    private static final byte[] IV = new byte[16]; // Initialization Vector (Keep this consistent for encryption/decryption)



    public static void encryptDirectory(String sourceDir, String encryptedZipPath) throws Exception {

        SecretKeySpec key = new SecretKeySpec(KEY_STRING.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));

        try (FileOutputStream fos = new FileOutputStream(encryptedZipPath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Path sourcePath = Paths.get(sourceDir);

            Files.walk(sourcePath).filter(Files::isRegularFile).forEach(filePath -> {
                try {
                    String relativePath = sourcePath.relativize(filePath).toString();
                    ZipEntry zipEntry = new ZipEntry(relativePath);
                    zos.putNextEntry(zipEntry);

                    try (CipherOutputStream cos = new CipherOutputStream(zos, cipher);
                         FileInputStream fis = new FileInputStream(filePath.toFile())) {

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            cos.write(buffer, 0, bytesRead);
                        }

                    }

                } catch (Exception e) {
                    throw new RuntimeException("Error encrypting file: " + filePath, e);
                }
            });
        }
    }

    public static void decryptZip(String encryptedZipPath, String outputDir) throws Exception {
        SecretKeySpec key = new SecretKeySpec(KEY_STRING.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));

        try (FileInputStream fis = new FileInputStream(encryptedZipPath);
             ZipInputStream zis = new ZipInputStream(fis)) {


            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(outputDir, zipEntry.getName());
                File parentDir = newFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }

                try (CipherInputStream cis = new CipherInputStream(zis, cipher);
                     FileOutputStream fos = new FileOutputStream(newFile)) {

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = cis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }

                }
            }


        }
    }


    public static void main(String[] args) throws Exception {
        String sourceDir = "html_files"; // Directory containing HTML files
        String encryptedZip = "encrypted_html.zip";
        String decryptedDir = "decrypted_html";

        encryptDirectory(sourceDir, encryptedZip);
        decryptZip(encryptedZip, decryptedDir);

        // Example of loading decrypted HTML in Swing
        //File htmlFile = new File(decryptedDir, "your_html_file.html"); // Adjust path as needed

       // JEditorPane editorPane = new JEditorPane();  // Example only - handle exceptions appropriately
       // editorPane.setEditable(false);
       // editorPane.setPage(htmlFile.toURI().toURL()); 
    }
}