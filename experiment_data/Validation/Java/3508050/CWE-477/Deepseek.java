import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class TrustedRootCertificates {

    public static void main(String[] args) {
        try {
            // Load the default truststore (cacerts)
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            String trustStorePath = System.getProperty("java.home") + "/lib/security/cacerts";
            char[] trustStorePassword = "changeit".toCharArray(); // Default password for cacerts

            trustStore.load(new java.io.FileInputStream(trustStorePath), trustStorePassword);

            // Iterate through the certificates in the truststore
            Enumeration<String> aliases = trustStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = trustStore.getCertificate(alias);

                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    System.out.println("Alias: " + alias);
                    System.out.println("Subject DN: " + x509Cert.getSubjectX500Principal());
                    System.out.println("Issuer DN: " + x509Cert.getIssuerX500Principal());
                    System.out.println("Serial Number: " + x509Cert.getSerialNumber());
                    System.out.println("Valid From: " + x509Cert.getNotBefore());
                    System.out.println("Valid Until: " + x509Cert.getNotAfter());
                    System.out.println("----------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}