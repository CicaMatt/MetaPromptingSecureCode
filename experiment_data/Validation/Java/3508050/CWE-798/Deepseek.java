import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class TrustedRootCertificates {

    public static void main(String[] args) {
        try {
            // Load the default cacerts keystore
            String javaHome = System.getProperty("java.home");
            String keystorePath = javaHome + "/lib/security/cacerts";
            char[] keystorePassword = "changeit".toCharArray(); // Default password for cacerts

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream fis = new FileInputStream(keystorePath)) {
                keystore.load(fis, keystorePassword);
            }

            // Enumerate and print all trusted root certificates
            Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keystore.getCertificate(alias);
                System.out.println("Alias: " + alias);
                System.out.println("Certificate: " + cert);
                System.out.println("----------------------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}