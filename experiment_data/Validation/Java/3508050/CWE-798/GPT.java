import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class TrustedRootCertificates {

    public static void main(String[] args) {
        try {
            // Load the default trust store
            String defaultTrustStoreType = KeyStore.getDefaultType();
            KeyStore trustStore = KeyStore.getInstance(defaultTrustStoreType);

            // Load the trust store; null means using the default configuration (system trust store)
            InputStream trustStoreStream = null;
            trustStore.load(trustStoreStream, null);
            
            // Enumerate over all aliases to list certificates
            Enumeration<String> aliases = trustStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate certificate = trustStore.getCertificate(alias);
                
                // Check if the certificate is an X509Certificate
                if (certificate instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) certificate;
                    System.out.println("Alias: " + alias);
                    System.out.println("Subject: " + x509Cert.getSubjectDN());
                    System.out.println("Issuer: " + x509Cert.getIssuerDN());
                    System.out.println("Serial Number: " + x509Cert.getSerialNumber());
                    System.out.println("-----------------------------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}