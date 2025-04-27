import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;

public class TrustedRootCertificates {

    public static List<X509Certificate> getTrustedRootCertificates() throws KeyStoreException, NoSuchAlgorithmException {
        List<X509Certificate> trustedRoots = new ArrayList<>();

        // Use the default KeyStore (cacerts) which contains the JRE's trusted roots
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // No password needed for the default KeyStore
        keyStore.load(null, null); 

        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate cert = keyStore.getCertificate(alias);
            if (cert instanceof X509Certificate) {
                trustedRoots.add((X509Certificate) cert);
            }
        }

        return trustedRoots;
    }

    public static void main(String[] args) {
        try {
            List<X509Certificate> rootCerts = getTrustedRootCertificates();
            for (X509Certificate cert : rootCerts) {
                System.out.println("Certificate: " + cert.getSubjectX500Principal());
                // Perform further processing or validation as needed, e.g., checking validity, etc.
            }
        } catch (KeyStoreException e) {
            System.err.println("Error accessing keystore: " + e.getMessage()); // Specific exception handling
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error with algorithm: " + e.getMessage()); // Specific exception handling
        }
    }
}