import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.util.Enumeration;

public class TrustedRootCertificates {

    public static void main(String[] args) throws Exception {

        // Get the default Java KeyStore which contains the trusted root certificates.
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null); // Load with null parameters to access the default keystore


        // Enumerate the entries (certificates) in the keystore
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();

            // Check if the entry is a certificate
            if (ks.isCertificateEntry(alias)) {
                Certificate cert = ks.getCertificate(alias);

                // Cast to X509Certificate for more detailed information
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;

                    // Print relevant information about the trusted root certificate
                    System.out.println("Alias: " + alias);
                    System.out.println("Subject DN: " + x509Cert.getSubjectDN());
                    System.out.println("Issuer DN: " + x509Cert.getIssuerDN());
                    System.out.println("Serial Number: " + x509Cert.getSerialNumber());
                    System.out.println("--------------------");
                }
            }
        }
    }
}