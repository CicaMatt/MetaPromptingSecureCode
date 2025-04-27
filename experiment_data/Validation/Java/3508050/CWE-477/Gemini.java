import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;

public class TrustedRootCertificates {

    public static void main(String[] args) {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null); // Load the default cacerts keystore

            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (ks.isCertificateEntry(alias)) {
                    Certificate cert = ks.getCertificate(alias);
                    if (cert instanceof X509Certificate) {
                        X509Certificate x509Cert = (X509Certificate) cert;
                        System.out.println("Alias: " + alias);
                        System.out.println("Subject DN: " + x509Cert.getSubjectDN());
                        // Add other certificate details as needed (e.g., issuer, validity)
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