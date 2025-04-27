import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class TrustedRootCertificates {

    public static void main(String[] args) {
        // Path to the default cacerts keystore
        String javaHome = System.getProperty("java.home");
        String keystorePath = javaHome + "/lib/security/cacerts";
        char[] keystorePassword = "changeit".toCharArray(); // Default password for cacerts

        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            // Load the keystore
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(fis, keystorePassword);

            // Enumerate all aliases in the keystore
            Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keystore.getCertificate(alias);

                // Print certificate details
                System.out.println("Alias: " + alias);
                System.out.println("Certificate Type: " + cert.getType());
                System.out.println("Certificate: " + cert);
                System.out.println("----------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}