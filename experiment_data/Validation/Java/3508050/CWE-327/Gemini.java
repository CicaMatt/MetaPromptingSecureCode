import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;

public class TrustedRootCertificates {

    public static void main(String[] args) {
        try {
            // Access the cacerts keystore which contains the JRE's trusted root certificates
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
            keyStore.load(null, null); // Load with default password (usually null)

            // Enumerate and print the trusted root certificates
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isCertificateEntry(alias)) { 
                    Certificate cert = keyStore.getCertificate(alias);
                    if (cert instanceof X509Certificate) {
                        X509Certificate x509Cert = (X509Certificate) cert;
                        System.out.println("Alias: " + alias);
                        System.out.println("Subject DN: " + x509Cert.getSubjectDN());
                        // Add more details as needed (e.g., issuer, validity)
                        System.out.println("-----");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error accessing trusted root certificates: " + e.getMessage());
            e.printStackTrace();
        }
    }
}