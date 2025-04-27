import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class TrustedCertificates {

    public static void main(String[] args) {
        try {
            // Get the default trusted CA certificates file
            String trustedCAsPath = System.getProperty("java.home") + "/lib/security/cacerts";

            // Load the JRE's default keystore
            FileInputStream is = new FileInputStream(trustedCAsPath);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, "changeit".toCharArray()); // Default password is 'changeit'

            // Iterate through all the certificates
            Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keystore.getCertificate(alias);

                // Display certificate details
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    System.out.println("Certificate: " + alias);
                    System.out.println("  Subject DN: " + x509Cert.getSubjectDN());
                    System.out.println("  Issuer DN: " + x509Cert.getIssuerDN());
                    System.out.println("  Serial Number: " + x509Cert.getSerialNumber());
                    System.out.println("  Valid From: " + x509Cert.getNotBefore());
                    System.out.println("  Valid Until: " + x509Cert.getNotAfter());
                    System.out.println("  Signature Algorithm: " + x509Cert.getSigAlgName());
                    System.out.println("-------------------------------------------");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}