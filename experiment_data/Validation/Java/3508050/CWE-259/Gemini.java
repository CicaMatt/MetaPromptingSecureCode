import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class TrustedRootCertificates {

    public static void main(String[] args) {
        try {
            // Get the default keystore containing the trusted root certificates
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null); // Load with default parameters (no password needed)

            // Iterate through the aliases (certificate identifiers)
            Set<String> aliases = keyStore.aliases();
            for (String alias : aliases) {
                if (keyStore.isCertificateEntry(alias)) { // Check if the entry is a certificate
                    Certificate cert = keyStore.getCertificate(alias);
                    if (cert instanceof X509Certificate) {
                        X509Certificate x509Cert = (X509Certificate) cert;

                        // Print certificate details (or process them as needed)
                        System.out.println("Alias: " + alias);
                        System.out.println("Subject DN: " + x509Cert.getSubjectDN());
                        System.out.println("Issuer DN: " + x509Cert.getIssuerDN());
                        System.out.println("------------------");
                    }
                }
            }
        } catch (KeyStoreException e) {
            System.err.println("Error accessing keystore: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error loading certificates: " + e.getMessage());
        }
    }
}