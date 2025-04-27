import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;

public class TrustedCertificates {

    public static void main(String[] args) {
        try {
            // Access the cacerts keystore (default truststore)
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null); // Load with default system properties

            // Iterate through the certificates
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isCertificateEntry(alias)) {
                    Certificate cert = keyStore.getCertificate(alias);
                    if (cert instanceof X509Certificate) {
                        X509Certificate x509Cert = (X509Certificate) cert;
                        System.out.println("Alias: " + alias);
                        System.out.println("Subject DN: " + x509Cert.getSubjectDN());
                        System.out.println("Issuer DN: " + x509Cert.getIssuerDN());
                        System.out.println("--------------------");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error accessing trusted certificates: " + e.getMessage());
            e.printStackTrace();
        }
    }
}