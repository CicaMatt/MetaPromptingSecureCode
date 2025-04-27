import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrustedRootCertificates {

    private static final Logger LOGGER = Logger.getLogger(TrustedRootCertificates.class.getName());

    public static void main(String[] args) {
        try {
            // Load the default keystore
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);

            // Iterate over all the aliases in the keystore
            Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keystore.getCertificate(alias);
                
                // Check if the certificate is an instance of X509Certificate
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    // Print the certificate details
                    System.out.println("Alias: " + alias);
                    System.out.println("Subject DN: " + x509Cert.getSubjectX500Principal());
                    System.out.println("Issuer DN: " + x509Cert.getIssuerX500Principal());
                    System.out.println("Serial Number: " + x509Cert.getSerialNumber());
                    System.out.println("Validity: " + x509Cert.getNotBefore() + " to " + x509Cert.getNotAfter());
                    System.out.println("------------------------------------------------");
                }
            }
        } catch (KeyStoreException e) {
            LOGGER.log(Level.SEVERE, "A KeyStore error occurred", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Algorithm not found", e);
        } catch (CertificateException e) {
            LOGGER.log(Level.SEVERE, "Certificate error encountered", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO error when loading the keystore", e);
        }
    }
}